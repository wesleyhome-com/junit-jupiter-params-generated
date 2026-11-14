#!/usr/bin/env bash
# Prints the body of one version's section of a Keep a Changelog file: everything between its
# "## [<version>]" heading and the next "## " heading, without surrounding blank lines.
# Fails when the section is missing or empty, so a release cannot go out with blank notes.
#
# Usage: changelog-section.sh <version> [changelog file]
set -euo pipefail

VERSION="${1:?version is required}"
CHANGELOG="${2:-CHANGELOG.md}"

section="$(awk -v heading="## [${VERSION}]" '
  index($0, heading) == 1 { found = 1; next }
  found && /^## / { exit }
  found { print }
' "${CHANGELOG}" | sed -e '/./,$!d' | sed -e ':a' -e '/^\n*$/{$d;N;ba' -e '}')"

if [[ -z "${section}" ]]; then
  echo "${CHANGELOG} has no entries under ## [${VERSION}]." >&2
  exit 1
fi

echo "${section}"
