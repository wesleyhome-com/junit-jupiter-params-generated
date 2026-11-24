#!/usr/bin/env bash
# Tests resolve-release-versions.sh, one case per line, each in a temporary directory. Prints the
# cases that fail. SCRIPT_UNDER_TEST runs the cases against another copy of the script.
#
# Usage: resolve-release-versions-test.sh
# shellcheck disable=SC2016 # The backticks in single quotes are the job summary's Markdown.
set -uo pipefail

SCRIPT="${SCRIPT_UNDER_TEST:-$(cd "$(dirname "$0")/.." && pwd)/resolve-release-versions.sh}"
WORK="$(mktemp -d)"
trap 'rm -rf "${WORK}"' EXIT

# The script reads only gradle.properties, so a call to gh or git fails the case.
mkdir -p "${WORK}/bin" "${WORK}/repo" "${WORK}/empty"
for tool in gh git; do
  printf '#!/usr/bin/env bash\necho "%s $*" >> "%s/called"\nexit 99\n' "${tool}" "${WORK}" > "${WORK}/bin/${tool}"
  chmod +x "${WORK}/bin/${tool}"
done
export PATH="${WORK}/bin:${PATH}"

cases=0
failures=0

# run <gradle.properties content, or @missing> <release_version> <next_version> [properties argument]
run() {
  local dir="${WORK}/repo"
  : > "${WORK}/called"
  : > "${WORK}/out"
  : > "${WORK}/summary"
  if [[ "$1" == @missing ]]; then
    dir="${WORK}/empty"
  else
    printf '%s\n' "$1" > "${dir}/gradle.properties"
  fi
  status=0
  stdout="$(cd "${dir}" && GITHUB_OUTPUT="${WORK}/out" GITHUB_STEP_SUMMARY="${WORK}/summary" \
    bash "${SCRIPT}" "$2" "$3" ${4:+"$4"} 2>&1)" || status=$?
  out="$(< "${WORK}/out")"
  summary="$(< "${WORK}/summary")"
}

# verdict <name> <problem, or empty when the case passed>
verdict() {
  cases=$((cases + 1))
  local problem="$2"
  if [[ -s "${WORK}/called" ]]; then
    problem+=" called $(< "${WORK}/called")."
  fi
  if [[ -n "${problem}" ]]; then
    failures=$((failures + 1))
    echo "FAIL $1:${problem}"
    echo "     exit ${status}, printed: ${stdout}"
  fi
}

# ok <name> <gradle.properties> <release_version> <next_version> <resolved release> <resolved next> [summary line...]
ok() {
  local name="$1" release="$5" next="$6" problem=""
  run "$2" "$3" "$4"
  shift 6
  (( status == 0 )) || problem+=" exited ${status}."
  [[ "${out}" == "release_version=${release}"$'\n'"next_version=${next}"$'\n'"tag=v${release}" ]] \
    || problem+=" wrote outputs: ${out//$'\n'/; }."
  for line in "$@"; do
    grep -qxF -- "${line}" <<< "${summary}" || problem+=" summary lacks: ${line}"
  done
  verdict "${name}" "${problem}"
}

# fails <name> <gradle.properties> <release_version> <next_version> <message>
fails() {
  local name="$1" message="$5" problem=""
  run "$2" "$3" "$4"
  (( status == 1 )) || problem+=" exited ${status}, not 1."
  [[ -z "${out}${summary}" ]] || problem+=" wrote outputs or a summary."
  [[ "${stdout}" == "::error::${message} Nothing was built or published." ]] \
    || problem+=" didn't print the one-line error: ::error::${message}"
  verdict "${name}" "${problem}"
}

P='version=4.1.2-SNAPSHOT
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
org.example.version=9.9.9'
BAD_VERSION="isn't a version such as 4.1.2 or 4.2.0-RC1."

# Blank inputs default from gradle.properties
ok "blank inputs" "${P}" "" "" 4.1.2 4.1.3-SNAPSHOT \
  '| Release | `4.1.2` | gradle.properties |' '| Tag | `v4.1.2` | release version |' \
  '| Next | `4.1.3-SNAPSHOT` | next patch version |'
ok "patch 9 becomes 10" 'version=4.1.9-SNAPSHOT' "" "" 4.1.9 4.1.10-SNAPSHOT
ok "multi-digit parts" 'version=10.20.30-SNAPSHOT' "" "" 10.20.30 10.20.31-SNAPSHOT
ok "zero parts" 'version=0.0.0-SNAPSHOT' "" "" 0.0.0 0.0.1-SNAPSHOT
ok "pre-release in gradle.properties" 'version=4.2.0-RC1-SNAPSHOT' "" "" 4.2.0-RC1 4.2.0-SNAPSHOT \
  '| Release | `4.2.0-RC1` | gradle.properties |' '| Next | `4.2.0-SNAPSHOT` | the version 4.2.0-RC1 leads up to |'

# Typed values win
ok "typed release and next" "${P}" 4.2.0 4.3.0-SNAPSHOT 4.2.0 4.3.0-SNAPSHOT \
  '| Release | `4.2.0` | typed |' '| Next | `4.3.0-SNAPSHOT` | typed |'
ok "typed release, blank next" "${P}" 4.2.0 "" 4.2.0 4.2.1-SNAPSHOT '| Next | `4.2.1-SNAPSHOT` | next patch version |'
ok "typed release equal to gradle.properties" "${P}" 4.1.2 "" 4.1.2 4.1.3-SNAPSHOT '| Release | `4.1.2` | typed |'
ok "blank release, next skips to a minor" "${P}" "" 4.2.0-SNAPSHOT 4.1.2 4.2.0-SNAPSHOT
ok "next skips to a major" "${P}" "" 5.0.0-SNAPSHOT 4.1.2 5.0.0-SNAPSHOT
ok "release skips to a major" "${P}" 5.0.0 "" 5.0.0 5.0.1-SNAPSHOT
ok "4.10 is after 4.9" "${P}" 4.9.0 4.10.0-SNAPSHOT 4.9.0 4.10.0-SNAPSHOT
ok "4.1.10 is after 4.1.9" "${P}" 4.1.9 4.1.10-SNAPSHOT 4.1.9 4.1.10-SNAPSHOT

# Pre-releases
ok "typed pre-release, blank next" "${P}" 4.2.0-RC1 "" 4.2.0-RC1 4.2.0-SNAPSHOT
ok "dotted pre-release" "${P}" 4.2.0-beta.1 "" 4.2.0-beta.1 4.2.0-SNAPSHOT
ok "pre-release with two dashes" "${P}" 4.2.0-RC-1 "" 4.2.0-RC-1 4.2.0-SNAPSHOT
ok "pre-release, next pre-release" "${P}" 4.2.0-RC1 4.2.0-RC2-SNAPSHOT 4.2.0-RC1 4.2.0-RC2-SNAPSHOT
ok "pre-release of the version main works towards" 'version=4.2.0-SNAPSHOT' 4.2.0-RC1 "" 4.2.0-RC1 4.2.0-SNAPSHOT
ok "pre-release of gradle.properties' patch" "${P}" 4.1.2-RC1 "" 4.1.2-RC1 4.1.2-SNAPSHOT
ok "two-dash pre-release of gradle.properties' patch" "${P}" 4.1.2-RC-1 "" 4.1.2-RC-1 4.1.2-SNAPSHOT
ok "earlier pre-release of the same version" 'version=4.2.0-RC2-SNAPSHOT' 4.2.0-RC1 "" 4.2.0-RC1 4.2.0-SNAPSHOT
fails "next is the pre-release's own snapshot" "${P}" 4.2.0-RC1 4.2.0-RC1-SNAPSHOT \
  "next_version 4.2.0-RC1-SNAPSHOT isn't newer than release_version 4.2.0-RC1."
fails "next before the pre-release" "${P}" 4.2.0-RC1 4.1.9-SNAPSHOT "next_version 4.1.9-SNAPSHOT isn't newer than release_version 4.2.0-RC1."

# gradle.properties
NOT_SNAPSHOT="which isn't a -SNAPSHOT version, so main isn't ready to release. Set it to the version being worked on, such as 4.1.2-SNAPSHOT, in a pull request."
fails "gradle.properties isn't a SNAPSHOT" 'version=4.1.2' "" "" "gradle.properties has version=4.1.2, ${NOT_SNAPSHOT}"
fails "gradle.properties isn't a SNAPSHOT, both typed" 'version=4.1.2' 4.1.3 4.1.4-SNAPSHOT "gradle.properties has version=4.1.2, ${NOT_SNAPSHOT}"
fails "gradle.properties lowercase snapshot" 'version=4.1.2-snapshot' "" "" "gradle.properties has version=4.1.2-snapshot, ${NOT_SNAPSHOT}"
fails "gradle.properties SNAPSHOT without a dash" 'version=4.1.2SNAPSHOT' "" "" "gradle.properties has version=4.1.2SNAPSHOT, ${NOT_SNAPSHOT}"
fails "gradle.properties missing" @missing "" "" "gradle.properties was not found."
fails "gradle.properties without version=" 'org.example.version=4.1.2-SNAPSHOT' "" "" "gradle.properties should have one version= line, and it has 0."
fails "gradle.properties with two version=" $'version=4.1.2-SNAPSHOT\nversion=4.1.3-SNAPSHOT' "" "" \
  "gradle.properties should have one version= line, and it has 2."
fails "gradle.properties version doesn't parse" 'version=4.1-SNAPSHOT' "" "" \
  "gradle.properties has version=4.1-SNAPSHOT, which isn't a version such as 4.1.2-SNAPSHOT."
fails "gradle.properties version doesn't parse, release typed" 'version=main-SNAPSHOT' 4.1.2 "" \
  "gradle.properties has version=main-SNAPSHOT, which isn't a version such as 4.1.2-SNAPSHOT."
fails "gradle.properties says SNAPSHOT twice" 'version=4.1.2-SNAPSHOT-SNAPSHOT' "" "" \
  "release_version 4.1.2-SNAPSHOT is a SNAPSHOT version. Give the version to publish, such as 4.1.2."
fails "gradle.properties says snapshot twice, lowercase first" 'version=4.1.2-snapshot-SNAPSHOT' "" "" \
  "release_version 4.1.2-snapshot is a SNAPSHOT version. Give the version to publish, such as 4.1.2."

# Typed release versions that don't parse
for bad in 4.1 v4.1.2 4.1.2. 4.01.2 01.1.2 4.1.02 4.1.2- "4.1.2 " " 4.1.2" 4.1.2-RC_1 "4.1.2-RC 1" \
  4.1.2--RC1 4.1.2-RC1. 4.1.2.3 x.y.z; do
  fails "release '${bad}' doesn't parse" "${P}" "${bad}" "" "release_version '${bad}' (typed) ${BAD_VERSION}"
done

# A release version that's a SNAPSHOT in any case, which Maven would treat as a snapshot
for snapshot in 4.1.2-SNAPSHOT 4.1.2-snapshot 4.1.2-Snapshot 4.2.0-RC1-snapshot 4.1.2-RC1SNAPSHOT; do
  fails "release ${snapshot} is a SNAPSHOT" "${P}" "${snapshot}" "" \
    "release_version ${snapshot} is a SNAPSHOT version. Give the version to publish, such as 4.1.2."
done

# A release version older than the one main works towards
OLDER="which main is working towards (gradle.properties has version=4.1.2-SNAPSHOT). Release 4.1.2 or a later version."
fails "release older patch" "${P}" 4.1.1 "" "release_version 4.1.1 is older than 4.1.2, ${OLDER}"
fails "release older minor" "${P}" 4.0.9 "" "release_version 4.0.9 is older than 4.1.2, ${OLDER}"
fails "release older major" "${P}" 3.9.9 "" "release_version 3.9.9 is older than 4.1.2, ${OLDER}"
fails "release older, with a newer next" "${P}" 4.0.9 4.2.0-SNAPSHOT "release_version 4.0.9 is older than 4.1.2, ${OLDER}"
fails "older pre-release" "${P}" 4.1.1-RC1 "" "release_version 4.1.1-RC1 is older than 4.1.2, ${OLDER}"
fails "4.9 is before 4.10" 'version=4.10.0-SNAPSHOT' 4.9.0 "" \
  "release_version 4.9.0 is older than 4.10.0, which main is working towards (gradle.properties has version=4.10.0-SNAPSHOT). Release 4.10.0 or a later version."
ok "4.10 is after 4.9 in gradle.properties" 'version=4.9.0-SNAPSHOT' 4.10.0 "" 4.10.0 4.10.1-SNAPSHOT

# Typed next versions that don't parse or aren't a SNAPSHOT
for bad in 4.1.3-SNAPSHOT. v4.1.3-SNAPSHOT 4.1-SNAPSHOT "4.1.3-SNAPSHOT " SNAPSHOT 4.01.3-SNAPSHOT; do
  fails "next '${bad}' doesn't parse" "${P}" "" "${bad}" "next_version '${bad}' isn't a version such as 4.1.3-SNAPSHOT."
done
for bad in 4.1.3 4.1.3-snapshot 4.1.3-RC1; do
  fails "next ${bad} isn't a -SNAPSHOT" "${P}" "" "${bad}" \
    "next_version ${bad} doesn't end in -SNAPSHOT. It's the version main is left at for further work."
done

# A next version that says SNAPSHOT twice
for twice in 4.3.0-SNAPSHOT-SNAPSHOT 4.3.0-snapshot-SNAPSHOT 4.3.0-RC1SNAPSHOT-SNAPSHOT; do
  fails "next ${twice} says SNAPSHOT twice" "${P}" "" "${twice}" \
    "next_version ${twice} says SNAPSHOT twice. Give a version such as 4.1.3-SNAPSHOT."
done
fails "next says SNAPSHOT twice after a pre-release" "${P}" 4.2.0-RC1 4.2.0-SNAPSHOT-SNAPSHOT \
  "next_version 4.2.0-SNAPSHOT-SNAPSHOT says SNAPSHOT twice. Give a version such as 4.1.3-SNAPSHOT."

# A next version that isn't newer
fails "next equals release" "${P}" "" 4.1.2-SNAPSHOT "next_version 4.1.2-SNAPSHOT isn't newer than release_version 4.1.2."
fails "next older than the default release" "${P}" "" 4.1.1-SNAPSHOT "next_version 4.1.1-SNAPSHOT isn't newer than release_version 4.1.2."
fails "next older patch" "${P}" 4.1.5 4.1.4-SNAPSHOT "next_version 4.1.4-SNAPSHOT isn't newer than release_version 4.1.5."
fails "next older minor" "${P}" 4.2.0 4.1.9-SNAPSHOT "next_version 4.1.9-SNAPSHOT isn't newer than release_version 4.2.0."
fails "next older major" "${P}" 4.2.0 3.9.9-SNAPSHOT "next_version 3.9.9-SNAPSHOT isn't newer than release_version 4.2.0."
fails "next 4.9 after 4.10" "${P}" 4.10.0 4.9.0-SNAPSHOT "next_version 4.9.0-SNAPSHOT isn't newer than release_version 4.10.0."
fails "next is a pre-release of the release" "${P}" 4.2.0 4.2.0-RC1-SNAPSHOT \
  "next_version 4.2.0-RC1-SNAPSHOT isn't newer than release_version 4.2.0."

# Typed values are escaped in the annotation, so they can't end it or start a command of their own
fails "newline in a typed value" "${P}" $'4.1.2\n::warning::injected' "" \
  "release_version '4.1.2%0A::warning::injected' (typed) ${BAD_VERSION}"
fails "carriage return in a typed value" "${P}" $'4.1.2\r' "" "release_version '4.1.2%0D' (typed) ${BAD_VERSION}"
fails "percent in a typed value" "${P}" '4.1.2%0A' "" "release_version '4.1.2%250A' (typed) ${BAD_VERSION}"
fails "newline in a typed next" "${P}" "" $'4.1.3-SNAPSHOT\n4.1.4-SNAPSHOT' \
  "next_version '4.1.3-SNAPSHOT%0A4.1.4-SNAPSHOT' isn't a version such as 4.1.3-SNAPSHOT."

# Files
mkdir -p "${WORK}/other"
printf 'version=7.0.0-SNAPSHOT\n' > "${WORK}/other/gradle.properties"
run "${P}" "" "" "${WORK}/other/gradle.properties"
problem=""
[[ "${status}" == 0 && "${out}" == $'release_version=7.0.0\nnext_version=7.0.1-SNAPSHOT\ntag=v7.0.0' ]] \
  || problem=" didn't use the file it was given."
verdict "reads the gradle.properties it's given" "${problem}"

printf 'earlier=1\n' > "${WORK}/kept-out"
printf '# Earlier step\n' > "${WORK}/kept-summary"
status=0
stdout="$(cd "${WORK}/other" && GITHUB_OUTPUT="${WORK}/kept-out" GITHUB_STEP_SUMMARY="${WORK}/kept-summary" \
  bash "${SCRIPT}" "" "" 2>&1)" || status=$?
problem=""
[[ "${status}" == 0 && "$(head -n 1 "${WORK}/kept-out")" == earlier=1 \
  && "$(head -n 1 "${WORK}/kept-summary")" == "# Earlier step" ]] || problem=" replaced what earlier steps wrote."
verdict "appends to GITHUB_OUTPUT and GITHUB_STEP_SUMMARY" "${problem}"

status=0
stdout="$(cd "${WORK}/other" && env -u GITHUB_OUTPUT -u GITHUB_STEP_SUMMARY bash "${SCRIPT}" "" "" 2>&1)" || status=$?
problem=""
[[ "${status}" == 0 \
  && "${stdout}" == "Releasing 7.0.0 (gradle.properties) as v7.0.0, then main moves to 7.0.1-SNAPSHOT (next patch version)." ]] \
  || problem=" failed."
verdict "runs without GITHUB_OUTPUT and GITHUB_STEP_SUMMARY" "${problem}"

mkdir -p "${WORK}/broken-sort"
printf '#!/usr/bin/env bash\nexit 2\n' > "${WORK}/broken-sort/sort"
chmod +x "${WORK}/broken-sort/sort"
: > "${WORK}/out"
status=0
stdout="$(cd "${WORK}/other" && PATH="${WORK}/broken-sort:${PATH}" GITHUB_OUTPUT="${WORK}/out" \
  bash "${SCRIPT}" "" "" 2>&1)" || status=$?
problem=""
[[ "${status}" == 1 && ! -s "${WORK}/out" \
  && "${stdout}" == "::error::Couldn't compare versions 7.0.0 and 7.0.0. Nothing was built or published." ]] \
  || problem=" didn't fail when versions couldn't be compared."
verdict "fails when sort fails" "${problem}"

if (( failures > 0 )); then
  echo "resolve-release-versions: ${failures} of ${cases} cases failed."
  exit 1
fi
echo "resolve-release-versions: all ${cases} cases passed."
