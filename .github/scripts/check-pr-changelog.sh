#!/usr/bin/env bash
# Checks a pull request against "Changelog and Labels" in DEVELOPERS.md and prints what to fix for
# every rule it breaks.
#
# Usage: check-pr-changelog.sh <base CHANGELOG.md> <pull request CHANGELOG.md>
# Reads the pull request's labels, one per line, from PR_LABELS.
set -euo pipefail

BASE="${1:?base changelog is required}"
HEAD="${2:?pull request changelog is required}"

has_label() {
  grep -qxF "$1" <<<"${PR_LABELS:-}"
}

# Prints the [Unreleased] section, or only its "### <subsection>" when one is given.
unreleased() {
  awk -v wanted="${2:-}" '
    /^## / { inside = index($0, "## [Unreleased]") == 1; in_wanted = 0; next }
    !inside { next }
    wanted == "" { print; next }
    /^### / { in_wanted = ($0 == "### " wanted); next }
    in_wanted { print }
  ' "$1"
}

problems=()

category_labels=(enhancement bug build documentation dependencies)
has_category=false
for label in "${category_labels[@]}" skip-changelog; do
  if has_label "${label}"; then
    has_category=true
  fi
done
if [[ "${has_category}" == false ]]; then
  problems+=("Add a label: enhancement, bug, build, documentation or dependencies, plus breaking if it applies. skip-changelog leaves the pull request out of the release notes instead.")
fi

if ! has_label skip-changelog && ! has_label dependencies \
  && [[ "$(unreleased "${BASE}")" == "$(unreleased "${HEAD}")" ]]; then
  problems+=("Add an entry to CHANGELOG.md under ## [Unreleased], in the section it belongs to: Upgrade notes, Added, Changed, Fixed or Build and docs. If the change needs no entry, label the pull request skip-changelog.")
fi

if has_label breaking \
  && [[ "$(unreleased "${BASE}" "Upgrade notes")" == "$(unreleased "${HEAD}" "Upgrade notes")" ]]; then
  problems+=("The pull request is labeled breaking, so add an entry under ### Upgrade notes in ## [Unreleased] that says what users have to change.")
fi

if (( ${#problems[@]} == 0 )); then
  echo "CHANGELOG.md and labels follow the rules in DEVELOPERS.md."
  exit 0
fi

for problem in "${problems[@]}"; do
  echo "::error title=Changelog and labels::${problem}"
done
exit 1
