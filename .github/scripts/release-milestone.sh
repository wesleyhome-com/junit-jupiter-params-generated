#!/usr/bin/env bash
# Checks or closes the milestone that plans a release. Milestones are titled after their version,
# such as 4.1.2. Pre-releases (anything but X.Y.Z) have none, so both modes skip them.
#
# check: runs before anything is built. Fails unless the milestone is closed or has no open issues
#   or pull requests. A missing milestone fails too: it most likely means a mistyped version. Any
#   API or parse error fails it as well: a lookup that can't be made doesn't count as a pass.
# close: runs after the release is published, which can't be undone, so it only ever warns.
#
# Usage: release-milestone.sh check|close <version>   (with GH_TOKEN and GITHUB_REPOSITORY set)
set -euo pipefail

MODE="${1:?mode is required: check or close}"
VERSION="${2:?version is required}"
REPO="${GITHUB_REPOSITORY:?GITHUB_REPOSITORY is required}"
MILESTONES_URL="https://github.com/${REPO}/milestones"

case "${MODE}" in
  check | close) ;;
  *)
    echo "Usage: release-milestone.sh check|close <version>" >&2
    exit 2
    ;;
esac

# Prints the milestone titled exactly VERSION as "<number> <state> <open count> <url>", or nothing.
# Its open count includes pull requests.
find_milestone() {
  local pages
  pages="$(gh api --paginate "repos/${REPO}/milestones?state=all&per_page=100")" || return 1
  jq -r -s --arg title "${VERSION}" '
    .[][] | select(.title == $title)
    | if (.number | type) == "number" and (.open_issues | type) == "number" then .
      else error("unexpected milestone: \(tojson)") end
    | "\(.number) \(.state) \(.open_issues) \(.html_url)"
  ' <<<"${pages}"
}

open_items() {
  local pages
  pages="$(gh api --paginate "repos/${REPO}/issues?milestone=$1&state=open&per_page=100")" || return 1
  jq -r -s '
    .[][]
    | "\(if has("pull_request") then "pull request" else "issue" end) #\(.number): \(.title)"
  ' <<<"${pages}"
}

# A workflow command ends at the first newline and decodes %-escapes, so both are encoded.
fail() {
  local message="$1"
  message="${message//'%'/'%25'}"
  message="${message//$'\n'/'%0A'}"
  echo "::error::${message}"
  exit 1
}

check() {
  local milestone number state open_count url items
  if ! milestone="$(find_milestone)"; then
    fail "Couldn't look up milestone ${VERSION} on GitHub. Nothing was built or published."
  fi
  if [[ -z "${milestone}" ]]; then
    fail "No milestone is titled ${VERSION}. Create it at ${MILESTONES_URL}, or run the release again with the right version. Nothing was built or published."
  fi
  read -r number state open_count url <<<"${milestone}"
  if [[ "${state}" == closed ]]; then
    echo "Milestone ${VERSION} is closed."
    return
  fi

  if ! items="$(open_items "${number}")"; then
    fail "Couldn't list the open issues and pull requests in milestone ${VERSION}. Nothing was built or published."
  fi
  # The milestone's count also catches items the listing misses, such as ones the token can't read.
  if [[ -n "${items}" ]] || (( open_count > 0 )); then
    fail "Milestone ${VERSION} has open issues or pull requests. Finish them or move them to another milestone, then run the release again. Nothing was built or published.
${items:-GitHub counts ${open_count}, but none of them could be listed.}
${url}"
  fi
  echo "Milestone ${VERSION} has no open issues or pull requests."
}

close() {
  local milestone number state url
  if ! milestone="$(find_milestone)" || [[ -z "${milestone}" ]]; then
    echo "::warning::Couldn't find milestone ${VERSION} to close it. The release is published; close it by hand: ${MILESTONES_URL}"
    return
  fi
  read -r number state _ url <<<"${milestone}"
  if [[ "${state}" == closed ]]; then
    echo "Milestone ${VERSION} was already closed."
    return
  fi

  if ! gh api --method PATCH "repos/${REPO}/milestones/${number}" -f state=closed > /dev/null; then
    echo "::warning::Couldn't close milestone ${VERSION}. The release is published; close it by hand: ${url}"
    return
  fi
  echo "Closed milestone ${VERSION}: ${url}"
}

if [[ ! "${VERSION}" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "${VERSION} is a pre-release, so it has no milestone to ${MODE}."
  exit 0
fi

"${MODE}"
