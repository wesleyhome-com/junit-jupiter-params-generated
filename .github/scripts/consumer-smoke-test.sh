#!/usr/bin/env bash
# Publishes this build into a throwaway repository, then builds and tests consumer-tests/kotlin-2.2
# against it: a Spring Boot 4.0 project compiled by Kotlin 2.2, with the annotation processor run by
# KSP. Any arguments, such as --no-daemon, are passed to both Gradle runs.
set -euo pipefail

cd "$(dirname "$0")/../.."
# Under Git Bash, pwd -W gives the Windows path that Gradle needs.
root="$(pwd -W 2> /dev/null || pwd)"
repository="$root/build/consumer-repository"
version="$(grep '^version=' gradle.properties | cut -d= -f2)"

rm -rf build/consumer-repository
./gradlew "-Dmaven.repo.local=$repository" \
  :validation:publishToMavenLocal \
  :annotation-processor:publishToMavenLocal \
  :junit-jupiter-params-generated:publishToMavenLocal \
  "$@"

# Spring Boot's dependency management sets the consumer's kotlin-stdlib and kotlin-reflect whatever
# the library asks for, so what the POMs and Gradle module metadata ask for is checked here.
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
if [[ "$(awk '{ print $2 }' <<< "$asked_for")" != "2.2.21" ]]; then
  echo "The published metadata should ask for kotlin-stdlib and kotlin-reflect 2.2.21 only." >&2
  exit 1
fi

log="build/consumer-smoke-test.log"
# The annotation processor writes no files, so after a first run Gradle finds KSP's task up to date
# even after a clean, and it wouldn't run again.
./gradlew -p consumer-tests/kotlin-2.2 test --rerun-tasks \
  "-PlibraryRepository=$repository" "-PlibraryVersion=$version" "$@" 2>&1 | tee "$log"
if ! grep -q "renamedOrder_filter\] looks like a filter" "$log"; then
  echo "The annotation processor's warning is missing from the consumer's build, so KSP didn't run it." >&2
  exit 1
fi
