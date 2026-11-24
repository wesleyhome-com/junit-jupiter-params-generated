#!/usr/bin/env bash
# Checks or closes the milestone that plans a release. Milestones are titled after their version,
# such as 4.1.2. Pre-releases (anything but X.Y.Z) have none, so both modes skip them.
#
# check: runs before anything is built. Fails unless the milestone, open or closed, has no open
#   issues or pull requests. A missing milestone fails too: it most likely means a mistyped version.
#   Any API or parse error fails it as well: a lookup that can't be made doesn't count as a pass.
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

# Prints the milestone titled exactly VERSION as "<number> <state> <open issues> <url>", or nothing.
# floor prints a whole number as digits; jq would print 1e2 as 1E+2.
find_milestone() {
  local pages
  pages="$(gh api --paginate "repos/${REPO}/milestones?state=all&per_page=100")" || return 1
  jq -r -s --arg title "${VERSION}" '
    def whole: type == "number" and . >= 0 and . == floor;
    .[][] | select(.title == $title)
    | if (.number | whole) and (.open_issues | whole) then .
      else error("unexpected milestone: \(tojson)") end
    | "\(.number | floor) \(.state) \(.open_issues | floor) \(.html_url)"
  ' <<<"${pages}"
}

# Prints each open issue and pull request in milestone number $1 as "<type> #<number>: <title>".
# Pull requests are listed from the pulls endpoint too: the issues listing leaves them out for a
# token that can't read pull requests.
open_items() {
  local issues pulls found
  issues="$(gh api --paginate "repos/${REPO}/issues?milestone=$1&state=open&per_page=100")" || return 1
  pulls="$(gh api --paginate "repos/${REPO}/pulls?state=open&per_page=100")" || return 1
  found="$(jq -c '.[] | {number, title, pr: has("pull_request")}' <<<"${issues}" \
    && jq -c --argjson milestone "$1" \
      '.[] | select(.milestone.number == $milestone) | {number, title, pr: true}' <<<"${pulls}")" \
    || return 1
  jq -r -s '
    unique_by(.number)[]
    | "\(if .pr then "pull request" else "issue" end) #\(.number): \(.title)"
  ' <<<"${found}"
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
  local milestone number open_count url items listed=0 report
  if ! milestone="$(find_milestone)"; then
    fail "Couldn't look up milestone ${VERSION} on GitHub. Nothing was built or published."
  fi
  if [[ -z "${milestone}" ]]; then
    fail "No milestone is titled ${VERSION}. Create it at ${MILESTONES_URL}, or run the release again with the right version. Nothing was built or published."
  fi
  read -r number _ open_count url <<<"${milestone}"

  if ! items="$(open_items "${number}")"; then
    fail "Couldn't list the open issues and pull requests in milestone ${VERSION}. Nothing was built or published."
  fi
  [[ -z "${items}" ]] || listed="$(wc -l <<<"${items}")"
  # The milestone's count also catches issues the listing misses.
  if (( listed == 0 )) && [[ "${open_count}" == 0 ]]; then
    echo "Milestone ${VERSION} has no open issues or pull requests."
    return
  fi

  report="Milestone ${VERSION} has open issues or pull requests. Finish them or move them to another milestone, then run the release again. Nothing was built or published."
  [[ -z "${items}" ]] || report+=$'\n'"${items}"
  if (( open_count > listed )); then
    report+=$'\n'"GitHub counts ${open_count} open issues in the milestone, but only ${listed} could be listed."
  fi
  fail "${report}"$'\n'"${url}"
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
