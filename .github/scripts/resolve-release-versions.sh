#!/usr/bin/env bash
# Works out the versions a release publishes and leaves main at, from the release workflow's inputs
# and gradle.properties, and fails before anything is built if they don't make sense.
#
# A blank release version is gradle.properties' version without -SNAPSHOT. A blank next version is
# the next patch version (4.1.2 -> 4.1.3-SNAPSHOT), or for a pre-release the version it leads up to
# (4.2.0-RC1 -> 4.2.0-SNAPSHOT). Typed versions are used as they are.
#
# Writes release_version, next_version and tag to $GITHUB_OUTPUT, and a table to
# $GITHUB_STEP_SUMMARY, when those are set. Tested by test/resolve-release-versions-test.sh.
#
# Usage: resolve-release-versions.sh <release version or ""> <next version or ""> [gradle.properties]
set -euo pipefail

TYPED_RELEASE="${1-}"
TYPED_NEXT="${2-}"
PROPERTIES="${3:-gradle.properties}"
VERSION_PATTERN='^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(-[0-9A-Za-z]+([.-][0-9A-Za-z]+)*)?$'
GA_PATTERN='^[0-9]+\.[0-9]+\.[0-9]+$'

# Typed values reach the message, so it's escaped as workflow commands require: a newline would
# otherwise end the annotation and start a line of its own.
fail() {
  local message="$1 Nothing was built or published."
  message="${message//'%'/%25}"
  message="${message//$'\r'/%0D}"
  message="${message//$'\n'/%0A}"
  echo "::error::${message}"
  exit 1
}

# Succeeds when version $1 comes before $2. "~" sorts before everything in version order, which
# puts 4.2.0-RC1 below 4.2.0.
older_than() {
  local sorted
  sorted="$(printf '%s\n' "$1" "$2" | sed 's/-/~/' | sort -V | sed 's/~/-/')" \
    || fail "Couldn't compare versions $1 and $2."
  [[ "$1" != "$2" && "${sorted}" == "$1"$'\n'"$2" ]]
}

if [[ ! -f "${PROPERTIES}" ]]; then
  fail "${PROPERTIES} was not found."
fi
mapfile -t found < <(sed -n 's/^version=//p' "${PROPERTIES}")
if (( ${#found[@]} != 1 )); then
  fail "${PROPERTIES} should have one version= line, and it has ${#found[@]}."
fi
SNAPSHOT="${found[0]}"
if [[ "${SNAPSHOT}" != *-SNAPSHOT ]]; then
  fail "${PROPERTIES} has version=${SNAPSHOT}, which isn't a -SNAPSHOT version, so main isn't ready to release. Set it to the version being worked on, such as 4.1.2-SNAPSHOT, in a pull request."
fi
if [[ ! "${SNAPSHOT}" =~ ${VERSION_PATTERN} ]]; then
  fail "${PROPERTIES} has version=${SNAPSHOT}, which isn't a version such as 4.1.2-SNAPSHOT."
fi

if [[ -n "${TYPED_RELEASE}" ]]; then
  RELEASE="${TYPED_RELEASE}"
  release_from="typed"
else
  RELEASE="${SNAPSHOT%-SNAPSHOT}"
  release_from="${PROPERTIES}"
fi
if [[ ! "${RELEASE}" =~ ${VERSION_PATTERN} ]]; then
  fail "release_version '${RELEASE}' (${release_from}) isn't a version such as 4.1.2 or 4.2.0-RC1."
fi
# Maven treats SNAPSHOT in any case as a snapshot, but the publishing plugin recognises only
# -SNAPSHOT, so it would upload and release 4.2.0-snapshot to Maven Central.
if [[ "${RELEASE^^}" == *SNAPSHOT ]]; then
  fail "release_version ${RELEASE} is a SNAPSHOT version. Give the version to publish, such as 4.1.2."
fi
if older_than "${RELEASE%%-*}" "${SNAPSHOT%%-*}"; then
  fail "release_version ${RELEASE} is older than ${SNAPSHOT%%-*}, which main is working towards (${PROPERTIES} has version=${SNAPSHOT}). Release ${SNAPSHOT%%-*} or a later version."
fi

if [[ -n "${TYPED_NEXT}" ]]; then
  NEXT="${TYPED_NEXT}"
  next_from="typed"
elif [[ "${RELEASE}" =~ ${GA_PATTERN} ]]; then
  NEXT="${RELEASE%.*}.$(( ${RELEASE##*.} + 1 ))-SNAPSHOT"
  next_from="next patch version"
else
  NEXT="${RELEASE%%-*}-SNAPSHOT"
  next_from="the version ${RELEASE} leads up to"
fi
if [[ ! "${NEXT}" =~ ${VERSION_PATTERN} ]]; then
  fail "next_version '${NEXT}' isn't a version such as 4.1.3-SNAPSHOT."
fi
if [[ "${NEXT}" != *-SNAPSHOT ]]; then
  fail "next_version ${NEXT} doesn't end in -SNAPSHOT. It's the version main is left at for further work."
fi
# A -SNAPSHOT is the work towards its version, so 4.1.2-SNAPSHOT comes before 4.1.2 and can't follow it.
NEXT_TARGET="${NEXT%-SNAPSHOT}"
if [[ "${NEXT_TARGET^^}" == *SNAPSHOT ]]; then
  fail "next_version ${NEXT} says SNAPSHOT twice. Give a version such as 4.1.3-SNAPSHOT."
fi
if ! older_than "${RELEASE}" "${NEXT_TARGET}"; then
  fail "next_version ${NEXT} isn't newer than release_version ${RELEASE}."
fi

TAG="v${RELEASE}"
echo "Releasing ${RELEASE} (${release_from}) as ${TAG}, then main moves to ${NEXT} (${next_from})."

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  {
    echo "release_version=${RELEASE}"
    echo "next_version=${NEXT}"
    echo "tag=${TAG}"
  } >> "${GITHUB_OUTPUT}"
fi
if [[ -n "${GITHUB_STEP_SUMMARY:-}" ]]; then
  {
    echo "### Release versions"
    echo
    echo "| | Version | From |"
    echo "|---|---|---|"
    echo "| Release | \`${RELEASE}\` | ${release_from} |"
    echo "| Tag | \`${TAG}\` | release version |"
    echo "| Next | \`${NEXT}\` | ${next_from} |"
  } >> "${GITHUB_STEP_SUMMARY}"
fi
