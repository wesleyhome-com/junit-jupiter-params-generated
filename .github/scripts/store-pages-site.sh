#!/usr/bin/env bash
# Replaces the contents of the checked-out gh-pages branch with the built site and pushes it, so the
# next build starts from every version published so far.
#
# Usage: store-pages-site.sh <site dir> <gh-pages checkout dir> <commit message>
set -euo pipefail

SITE_DIR="${1:?site dir is required}"
STORED_DIR="${2:?gh-pages checkout dir is required}"
MESSAGE="${3:?commit message is required}"

find "${STORED_DIR}" -mindepth 1 -maxdepth 1 ! -name .git -exec rm -rf {} +
cp -R "${SITE_DIR}/." "${STORED_DIR}/"

cd "${STORED_DIR}"
git config user.name "github-actions[bot]"
git config user.email "41898282+github-actions[bot]@users.noreply.github.com"
git add -A
if git diff --cached --quiet; then
  echo "Stored site is already up to date."
  exit 0
fi
git commit -q -m "${MESSAGE}"
git push -q origin HEAD:gh-pages
