#!/usr/bin/env bash
# Tests release-milestone.sh against a stub of `gh api` that serves milestones, issues and pull
# requests for owner/repo from each case's fixtures, without the network. Prints the cases that fail.
# SCRIPT_UNDER_TEST runs the cases against another copy of the script.
#
# Usage: release-milestone-test.sh
set -uo pipefail

SCRIPT="${SCRIPT_UNDER_TEST:-$(cd "$(dirname "$0")/.." && pwd)/release-milestone.sh}"
WORK="$(mktemp -d)"
trap 'rm -rf "${WORK}"' EXIT
mkdir -p "${WORK}/bin"

# Switches, as files in the case directory: fail-<endpoint> (HTTP 500), drop-<endpoint> (no
# connection), raw-<endpoint> (printed as the response), hide-prs (the issues listing leaves out
# pull requests), page-size, merge-pages (one array for every page, as newer gh prints).
# Endpoints: milestones, issues, pulls, patch.
cat > "${WORK}/bin/gh" <<'STUB'
#!/usr/bin/env bash
set -euo pipefail
echo "$*" >> "${CASE_DIR}/calls"
[[ "${1:-}" == api ]] || exit 64
shift
paginate=false method=GET endpoint="" field=""
while (( $# > 0 )); do
  case "$1" in
    --paginate) paginate=true ;;
    --method) method="$2"; shift ;;
    -f) field="$2"; shift ;;
    -*) exit 64 ;;
    *) endpoint="$1" ;;
  esac
  shift
done
path="${endpoint%%\?*}"
query="&&"
[[ "${endpoint}" != *\?* ]] || query="&${endpoint#*\?}&"
param() {
  local rest="${query#*&"$1"=}"
  if [[ "${rest}" == "${query}" ]]; then echo "$2"; else echo "${rest%%&*}"; fi
}
http_error() {
  echo "{\"message\":\"$2\"}"
  echo "gh: $2 (HTTP $1)" >&2
  exit 1
}
case "${method} ${path}" in
  "GET repos/owner/repo/milestones") name="milestones" ;;
  "GET repos/owner/repo/issues") name="issues" ;;
  "GET repos/owner/repo/pulls") name="pulls" ;;
  "PATCH repos/owner/repo/milestones/"*) name="patch" ;;
  *) http_error 404 "Not Found" ;;
esac
[[ ! -f "${CASE_DIR}/fail-${name}" ]] || http_error 500 "Server Error"
if [[ -f "${CASE_DIR}/drop-${name}" ]]; then
  echo "error connecting to api.github.com" >&2
  exit 1
fi
if [[ -f "${CASE_DIR}/raw-${name}" ]]; then
  cat "${CASE_DIR}/raw-${name}"
  exit 0
fi

state="$(param state open)"
case "${name}" in
  milestones)
    selected="$(jq -cs --arg state "${state}" \
      'map(select($state == "all" or .state == $state))' "${CASE_DIR}/milestones")" ;;
  issues)
    milestone="$(param milestone any)"
    [[ "${milestone}" == any || "${milestone}" =~ ^[0-9]+$ ]] || http_error 422 "Validation Failed"
    hide=false
    [[ ! -f "${CASE_DIR}/hide-prs" ]] || hide=true
    selected="$(jq -cs --arg milestone "${milestone}" --arg state "${state}" --argjson hide "${hide}" '
      map(select(($milestone == "any" or (.milestone.number | tostring) == $milestone)
        and ($state == "all" or .state == $state) and (($hide and has("pull_request")) | not)))
    ' "${CASE_DIR}/items")" ;;
  pulls)
    selected="$(jq -cs --arg state "${state}" \
      'map(select(has("pull_request") and ($state == "all" or .state == $state)))' "${CASE_DIR}/items")" ;;
  patch)
    number="${path##*/}"
    [[ "${field}" == state=closed ]] || exit 64
    jq -se --argjson n "${number}" 'any(.[]; .number == $n)' "${CASE_DIR}/milestones" > /dev/null \
      || http_error 404 "Not Found"
    jq -c --argjson n "${number}" 'if .number == $n then .state = "closed" else . end' \
      "${CASE_DIR}/milestones" > "${CASE_DIR}/milestones.new"
    mv "${CASE_DIR}/milestones.new" "${CASE_DIR}/milestones"
    jq -c --argjson n "${number}" 'select(.number == $n)' "${CASE_DIR}/milestones"
    exit 0 ;;
esac

size="$(cat "${CASE_DIR}/page-size" 2>/dev/null || param per_page 30)"
if [[ "${paginate}" == true && -f "${CASE_DIR}/merge-pages" ]]; then
  echo "${selected}"
  exit 0
fi
count="$(jq length <<<"${selected}")"
for (( from = 0; from == 0 || from < count; from += size )); do
  jq -c --argjson from "${from}" --argjson size "${size}" '.[$from:$from + $size]' <<<"${selected}"
  [[ "${paginate}" == true ]] || break
done
STUB
chmod +x "${WORK}/bin/gh"

# A native Windows jq writes CRLF line ends, which the Linux runners' jq doesn't.
case "$(uname -s)" in
  MINGW* | MSYS* | CYGWIN*)
    printf '#!/usr/bin/env bash\nexec "%s" -b "$@"\n' "$(command -v jq)" > "${WORK}/bin/jq"
    chmod +x "${WORK}/bin/jq"
    ;;
esac
export PATH="${WORK}/bin:${PATH}"

cases=0
failures=0
name=""
problem=""
status=0
stdout=""
CASE_DIR=""

start() {
  finish
  name="$1"
  problem=""
  CASE_DIR="${WORK}/case-${cases}"
  mkdir -p "${CASE_DIR}"
  : > "${CASE_DIR}/milestones"
  : > "${CASE_DIR}/items"
  : > "${CASE_DIR}/calls"
}

finish() {
  [[ -n "${name}" ]] || return 0
  cases=$((cases + 1))
  if [[ -n "${problem}" ]]; then
    failures=$((failures + 1))
    echo "FAIL ${name}:${problem}"
    echo "     exit ${status}, printed: ${stdout}"
  fi
  name=""
}

# milestone <number> <title> <state> <open_issues>
milestone() {
  printf '{"number":%s,"title":"%s","state":"%s","open_issues":%s,"html_url":"https://github.com/owner/repo/milestone/%s"}\n' \
    "$1" "$2" "$3" "$4" "$1" >> "${CASE_DIR}/milestones"
}

# issue|pr <number> <title> <state> <milestone number, or null>
item() {
  local milestone="null" pull_request=""
  [[ "$5" == null ]] || milestone="{\"number\":$5}"
  [[ "$1" == issue ]] || pull_request=',"pull_request":{}'
  printf '{"number":%s,"title":"%s","state":"%s","milestone":%s%s}\n' \
    "$2" "$3" "$4" "${milestone}" "${pull_request}" >> "${CASE_DIR}/items"
}
issue() { item issue "$@"; }
pr() { item pr "$@"; }

switch() { printf '%s' "${2:-}" > "${CASE_DIR}/$1"; }

run() {
  status=0
  stdout="$(CASE_DIR="${CASE_DIR}" GITHUB_REPOSITORY=owner/repo GH_TOKEN=token bash "${SCRIPT}" "$@" 2> "${CASE_DIR}/stderr")" \
    || status=$?
}

# exits <status> <mode> <version> <exact output>
exits() {
  run "$2" "$3"
  [[ "${status}" == "$1" ]] || problem+=" exited ${status}, not $1."
  [[ "${stdout}" == "$4" ]] || problem+=" printed something other than: $4"
}

called() { grep -qF -- "$1" "${CASE_DIR}/calls" || problem+=" didn't call gh $1."; }
not_called() { ! grep -qF -- "$1" "${CASE_DIR}/calls" || problem+=" called gh $1."; }
no_calls() { [[ ! -s "${CASE_DIR}/calls" ]] || problem+=" called gh: $(< "${CASE_DIR}/calls")"; }
state_is() {
  local state
  state="$(jq -r --argjson n "$1" 'select(.number == $n) | .state' "${CASE_DIR}/milestones")"
  [[ "${state}" == "$2" ]] || problem+=" left milestone $1 ${state}, not $2."
}

# Milestones and items around 4.1.2 that only nearly match it.
neighbours() {
  milestone 1 4.1.1 closed 0
  milestone 3 4.1.20 open 1
  milestone 4 v4.1.2 open 1
  milestone 5 4.1.2-RC1 open 1
  issue 90 "In 4.1.20" open 3
  pr 91 "In v4.1.2" open 4
  issue 92 "In 4.1.2-RC1" open 5
  pr 93 "Without a milestone" open null
}

OPEN="::error::Milestone 4.1.2 has open issues or pull requests. Finish them or move them to another milestone, then run the release again. Nothing was built or published."
URL="%0Ahttps://github.com/owner/repo/milestone/2"
NONE="Milestone 4.1.2 has no open issues or pull requests."
LOOKUP="::error::Couldn't look up milestone 4.1.2 on GitHub. Nothing was built or published."
LIST="::error::Couldn't list the open issues and pull requests in milestone 4.1.2. Nothing was built or published."
NOT_FOUND="::warning::Couldn't find milestone 4.1.2 to close it. The release is published; close it by hand: https://github.com/owner/repo/milestones"
NOT_CLOSED="::warning::Couldn't close milestone 4.1.2. The release is published; close it by hand: https://github.com/owner/repo/milestone/2"
CLOSED="Closed milestone 4.1.2: https://github.com/owner/repo/milestone/2"

# check: what's open
start "open issues only"
neighbours; milestone 2 4.1.2 open 2
issue 11 "Fails at 100% coverage" open 2; issue 12 "Second issue" open 2
issue 13 "Closed issue" closed 2; pr 14 "Merged pull request" closed 2
exits 1 check 4.1.2 "${OPEN}%0Aissue #11: Fails at 100%25 coverage%0Aissue #12: Second issue${URL}"
called "api --paginate repos/owner/repo/milestones?state=all&per_page=100"
called "api --paginate repos/owner/repo/issues?milestone=2&state=open&per_page=100"
called "api --paginate repos/owner/repo/pulls?state=open&per_page=100"

start "open pull requests only"
neighbours; milestone 2 4.1.2 open 0
pr 31 "Open pull request" open 2; issue 32 "Closed issue" closed 2
exits 1 check 4.1.2 "${OPEN}%0Apull request #31: Open pull request${URL}"

start "open issues and pull requests, in number order"
neighbours; milestone 2 4.1.2 open 2
pr 42 "Open pull request" open 2; issue 43 "Later issue" open 2; issue 41 "Open issue" open 2
exits 1 check 4.1.2 "${OPEN}%0Aissue #41: Open issue%0Apull request #42: Open pull request%0Aissue #43: Later issue${URL}"

start "everything closed"
neighbours; milestone 2 4.1.2 open 0
issue 51 "Closed issue" closed 2; pr 52 "Merged pull request" closed 2
exits 0 check 4.1.2 "${NONE}"

start "empty milestone"
neighbours; milestone 2 4.1.2 open 0
exits 0 check 4.1.2 "${NONE}"

start "closed milestone with nothing open"
neighbours; milestone 2 4.1.2 closed 0; issue 61 "Closed issue" closed 2
exits 0 check 4.1.2 "${NONE}"
called "issues?milestone=2&state=open"

start "closed milestone with open items"
neighbours; milestone 2 4.1.2 closed 1
issue 62 "Left open" open 2; pr 63 "Still open" open 2
exits 1 check 4.1.2 "${OPEN}%0Aissue #62: Left open%0Apull request #63: Still open${URL}"

# check: pull requests the issues listing leaves out
start "open pull request left out of the issues listing and the count"
neighbours; milestone 2 4.1.2 open 0; pr 71 "Hidden pull request" open 2; issue 72 "Closed issue" closed 2
switch hide-prs
exits 1 check 4.1.2 "${OPEN}%0Apull request #71: Hidden pull request${URL}"

start "pull requests can't be read at all"
neighbours; milestone 2 4.1.2 open 0; pr 73 "Hidden pull request" open 2
switch hide-prs; switch fail-pulls
exits 1 check 4.1.2 "${LIST}"

start "pull request in both listings is named once"
milestone 2 4.1.2 open 0; pr 74 "Listed twice" open 2
exits 1 check 4.1.2 "${OPEN}%0Apull request #74: Listed twice${URL}"

# check: the milestone's count
start "count with nothing listed"
milestone 2 4.1.2 open 1
exits 1 check 4.1.2 "${OPEN}%0AGitHub counts 1 open issues in the milestone, but only 0 could be listed.${URL}"

start "count above what's listed"
milestone 2 4.1.2 open 3; issue 81 "Visible issue" open 2
exits 1 check 4.1.2 "${OPEN}%0Aissue #81: Visible issue%0AGitHub counts 3 open issues in the milestone, but only 1 could be listed.${URL}"

start "listed above the count"
milestone 2 4.1.2 open 0; issue 82 "Counted late" open 2
exits 1 check 4.1.2 "${OPEN}%0Aissue #82: Counted late${URL}"

start "count written with an exponent"
milestone 2 4.1.2 open 1e2
exits 1 check 4.1.2 "${OPEN}%0AGitHub counts 100 open issues in the milestone, but only 0 could be listed.${URL}"

start "zero count written as 0.0"
milestone 2 4.1.2 open 0.0
exits 0 check 4.1.2 "${NONE}"

for bad in 0.5 -1 '"0"' null; do
  start "count ${bad}"
  milestone 2 4.1.2 open "${bad}"
  exits 1 check 4.1.2 "${LOOKUP}"
  not_called issues
done

for bad in 2.5 -2 '"2"' null; do
  start "milestone number ${bad}"
  printf '{"number":%s,"title":"4.1.2","state":"open","open_issues":0,"html_url":"u"}\n' "${bad}" > "${CASE_DIR}/milestones"
  exits 1 check 4.1.2 "${LOOKUP}"
done

start "milestone number written as 2.0"
printf '{"number":2.0,"title":"4.1.2","state":"open","open_issues":1,"html_url":"https://github.com/owner/repo/milestone/2"}\n' \
  > "${CASE_DIR}/milestones"
issue 11 "Open issue" open 2
exits 1 check 4.1.2 "${OPEN}%0Aissue #11: Open issue${URL}"
called "issues?milestone=2&state=open"

start "milestone without a number or count"
printf '{"title":"4.1.2","state":"open","html_url":"u"}\n' > "${CASE_DIR}/milestones"
exits 1 check 4.1.2 "${LOOKUP}"

# check: which milestone
start "no milestone with the version's exact title"
neighbours
exits 1 check 4.1.2 "::error::No milestone is titled 4.1.2. Create it at https://github.com/owner/repo/milestones, or run the release again with the right version. Nothing was built or published."
not_called issues

start "no milestones at all"
exits 1 check 4.1.2 "::error::No milestone is titled 4.1.2. Create it at https://github.com/owner/repo/milestones, or run the release again with the right version. Nothing was built or published."

for version in 4.2.0-RC1 4.2.0-SNAPSHOT 4.2 4.2.0.1 v4.2.0 x4.2.0 .2.0 4..0 4.2. 4-2-0; do
  start "pre-release ${version}"
  milestone 2 "${version}" open 1; issue 21 "Open issue" open 2
  exits 0 check "${version}" "${version} is a pre-release, so it has no milestone to check."
  no_calls
done

# check: pagination
for pages in page-by-page merged; do
  start "pagination, pages printed ${pages}"
  switch page-size 2
  [[ "${pages}" == page-by-page ]] || switch merge-pages
  milestone 1 4.0.0 closed 0; milestone 3 4.1.0 closed 0; milestone 4 4.1.1 closed 0
  milestone 5 4.1.3 open 0; milestone 2 4.1.2 open 3
  pr 106 "Elsewhere" open 5; pr 107 "Also elsewhere" open null
  issue 101 "Page one a" open 2; issue 102 "Page one b" open 2; pr 103 "Page two a" open 2
  issue 104 "Page two b" open 2; pr 105 "Page three" open 2
  # With pull requests left out of the issues listing, each listing has a match on its last page.
  switch hide-prs
  exits 1 check 4.1.2 "${OPEN}%0Aissue #101: Page one a%0Aissue #102: Page one b%0Apull request #103: Page two a%0Aissue #104: Page two b%0Apull request #105: Page three${URL}"
done

start "pagination with everything closed"
switch page-size 1
milestone 1 4.1.1 closed 0; milestone 2 4.1.2 open 0
issue 111 "Closed a" closed 2; issue 112 "Closed b" closed 2; pr 113 "Elsewhere" open 1; pr 114 "Also elsewhere" open 1
exits 0 check 4.1.2 "${NONE}"

# check: errors never count as a pass
for endpoint in milestones issues pulls; do
  expected="${LIST}"
  [[ "${endpoint}" != milestones ]] || expected="${LOOKUP}"
  for kind in fail drop; do
    start "${endpoint}: ${kind}"
    milestone 2 4.1.2 open 0
    switch "${kind}-${endpoint}"
    exits 1 check 4.1.2 "${expected}"
  done
  for raw in '[{"number": 2, "title": "4.1.2", "state": "open", "open_issues": 0' '<html>Unicorn!</html>' \
      '{"message": "Bad credentials"}' '[7]' '["4.1.2"]'; do
    start "${endpoint}: response ${raw}"
    milestone 2 4.1.2 open 0
    switch "raw-${endpoint}" "${raw}"
    exits 1 check 4.1.2 "${expected}"
  done
  # jq prints what it read before the element it fails on.
  start "${endpoint}: response that fails part-way"
  milestone 2 4.1.2 closed 0
  switch "raw-${endpoint}" '[{"number": 2, "title": "4.1.2", "state": "closed", "open_issues": 0, "html_url": "u", "milestone": {"number": 2}}, 7]'
  exits 1 check 4.1.2 "${expected}"
done

# close
start "close"
neighbours; milestone 2 4.1.2 open 0
exits 0 close 4.1.2 "${CLOSED}"
called "api --method PATCH repos/owner/repo/milestones/2 -f state=closed"
state_is 2 closed; state_is 3 open; state_is 4 open; state_is 5 open

start "close a milestone on a later page"
switch page-size 1
milestone 1 4.1.1 closed 0; milestone 3 4.1.3 open 0; milestone 2 4.1.2 open 0
exits 0 close 4.1.2 "${CLOSED}"
state_is 2 closed; state_is 3 open

start "close a closed milestone"
neighbours; milestone 2 4.1.2 closed 0
exits 0 close 4.1.2 "Milestone 4.1.2 was already closed."
not_called PATCH

start "close with open items"
milestone 2 4.1.2 open 1; issue 121 "Open issue" open 2
exits 0 close 4.1.2 "${CLOSED}"

start "close: no milestone"
neighbours
exits 0 close 4.1.2 "${NOT_FOUND}"
not_called PATCH
state_is 3 open; state_is 4 open; state_is 5 open

for kind in fail drop; do
  start "close: milestones ${kind}"
  milestone 2 4.1.2 open 0; switch "${kind}-milestones"
  exits 0 close 4.1.2 "${NOT_FOUND}"
  not_called PATCH

  start "close: update ${kind}"
  milestone 2 4.1.2 open 0; switch "${kind}-patch"
  exits 0 close 4.1.2 "${NOT_CLOSED}"
  state_is 2 open
done

start "close: unreadable milestones"
milestone 2 4.1.2 open 0; switch raw-milestones 'not json'
exits 0 close 4.1.2 "${NOT_FOUND}"
not_called PATCH

start "close: milestones response that fails part-way"
milestone 2 4.1.2 open 0
switch raw-milestones '[{"number": 2, "title": "4.1.2", "state": "open", "open_issues": 0, "html_url": "u"}, 7]'
exits 0 close 4.1.2 "${NOT_FOUND}"
not_called PATCH
state_is 2 open

start "close a pre-release"
milestone 2 4.2.0-RC1 open 0
exits 0 close 4.2.0-RC1 "4.2.0-RC1 is a pre-release, so it has no milestone to close."
no_calls
state_is 2 open

# usage
start "unknown mode"
milestone 2 4.1.2 open 0
exits 2 verify 4.1.2 ""
no_calls

start "unknown mode with a pre-release"
exits 2 verify 4.1.2-RC1 ""

start "no version"
run check
[[ "${status}" != 0 ]] || problem+=" exited 0."
no_calls

start "no GITHUB_REPOSITORY"
milestone 2 4.1.2 open 0
status=0
stdout="$(CASE_DIR="${CASE_DIR}" GH_TOKEN=token env -u GITHUB_REPOSITORY bash "${SCRIPT}" check 4.1.2 2>&1)" || status=$?
[[ "${status}" != 0 ]] || problem+=" exited 0."
no_calls

finish
if (( failures > 0 )); then
  echo "release-milestone: ${failures} of ${cases} cases failed."
  exit 1
fi
echo "release-milestone: all ${cases} cases passed."
