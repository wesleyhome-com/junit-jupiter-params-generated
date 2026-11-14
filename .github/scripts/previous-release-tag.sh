#!/usr/bin/env bash
# Prints the tag a release's notes should start from, or nothing for the first release.
#
# A GA release (vX.Y.Z) starts from the previous GA release, so its notes cover everything since then,
# including any pre-releases in between. A pre-release starts from the tag just before it.
#
# Usage: previous-release-tag.sh <tag>   (reads the repository's v* tags)
set -euo pipefail

TAG="${1:?tag is required}"
GA_PATTERN='^v[0-9]+\.[0-9]+\.[0-9]+$'

# "~" sorts before everything in version order, which puts v4.1.0-RC1 below v4.1.0.
mapfile -t TAGS < <({ git tag --list 'v*'; echo "${TAG}"; } | sort -u \
  | sed 's/-/~/' | sort -V | sed 's/~/-/')

previous=""
for t in "${TAGS[@]}"; do
  if [[ "${t}" == "${TAG}" ]]; then
    break
  fi
  if [[ ! "${TAG}" =~ ${GA_PATTERN} || "${t}" =~ ${GA_PATTERN} ]]; then
    previous="${t}"
  fi
done

echo "${previous}"
