#!/usr/bin/env bash
# Publishes this build into a throwaway repository, then builds and tests consumer-tests/kotlin-2.2
# against it, a Kotlin 2.2 project with the annotation processor run by KSP: once with Spring Boot
# 4.0's dependency management, and once without. Any arguments, such as --no-daemon, are passed to
# every Gradle run.
set -euo pipefail

cd "$(dirname "$0")/../.."
# Under Git Bash, pwd -W gives the Windows path that Gradle needs.
root="$(pwd -W 2> /dev/null || pwd)"
repository="$root/build/consumer-repository"
version="$(grep '^version=' gradle.properties | cut -d= -f2)"
kotlin="$(grep '^kotlin-gradle-plugin = "' gradle/libs.versions.toml | cut -d'"' -f2)"
gradle_args=("$@")

rm -rf build/consumer-repository
./gradlew "-Dmaven.repo.local=$repository" \
  :validation:publishToMavenLocal \
  :annotation-processor:publishToMavenLocal \
  :junit-jupiter-params-generated:publishToMavenLocal \
  "${gradle_args[@]}"

# Only the class metadata is built for Kotlin 2.2. The POMs and module metadata ask for the
# compiler's kotlin-stdlib and kotlin-reflect, so upgrading changes no project's Kotlin libraries.
published="build/consumer-repository/com/wesleyhome/test"
asked_for="$(
  {
    cat "$published"/*/"$version"/*.pom \
      | grep -A1 -E '<artifactId>kotlin-(stdlib|reflect)</artifactId>' | grep -oE '<version>[^<]+' | cut -d'>' -f2
    cat "$published"/*/"$version"/*.module \
      | grep -A2 -E '"module": "kotlin-(stdlib|reflect)"' | grep -oE '"requires": "[^"]+' | cut -d'"' -f4
  } | sort | uniq -c
)"
echo "kotlin-stdlib and kotlin-reflect versions the published metadata asks for:"
echo "$asked_for"
if [[ "$(awk '{ print $2 }' <<< "$asked_for")" != "$kotlin" ]]; then
  echo "The published metadata should ask for kotlin-stdlib and kotlin-reflect $kotlin only." >&2
  exit 1
fi

warning='renamedOrder_filter\] looks like a filter'
consumer() {
  local name="$1"
  shift
  local log="build/consumer-smoke-test-$name.log"
  echo "### Kotlin 2.2 consumer: $name"
  # The annotation processor writes no files, so after a first run Gradle finds KSP's task up to
  # date even after a clean, and it wouldn't run again.
  ./gradlew -p consumer-tests/kotlin-2.2 test --rerun-tasks \
    "-PlibraryRepository=$repository" "-PlibraryVersion=$version" "$@" "${gradle_args[@]}" 2>&1 | tee "$log"
  if ! grep -q "$warning" "$log"; then
    echo "The annotation processor's warning is missing from the $name build, so KSP didn't run it." >&2
    exit 1
  fi
  if grep -E '^(w|e): ' "$log" | grep -v "$warning"; then
    echo "The $name build reported the compiler or KSP messages above; only the processor's warning is expected." >&2
    exit 1
  fi
}

# Spring Boot 4.0's dependency management sets kotlin-stdlib, kotlin-reflect and JUnit to its own.
consumer spring-boot-4.0 -PspringBoot=4.0.9 -PkotlinRuntime=2.2.21 -PjunitVersion=6.0.3
# Without it, Gradle resolves the newer kotlin-stdlib and kotlin-reflect the library asks for, at run
# time and for KSP. The Kotlin 2.2 compiler never sees them.
consumer without-spring-boot "-PkotlinRuntime=$kotlin"
