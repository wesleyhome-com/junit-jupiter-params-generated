#!/usr/bin/env bash
# Fails when the release could not push its commits and tag. The release publishes to Maven Central,
# which can't be undone, before it pushes, so this runs before the build and again just before the
# upload.
#
# The dry-run push proves the credentials (the deploy key in the release workflow) may push to the
# repository. It sends no ref updates, so GitHub never evaluates main's ruleset: a deploy key that
# has lost its bypass still passes.
#
# Usage: check-release-push.sh <tag>   (run in the release checkout of main)
set -euo pipefail

TAG="${1:?tag is required}"

checked_out="$(git rev-parse HEAD)"
remote_main="$(git ls-remote origin refs/heads/main | cut -f1)"
if [[ "${remote_main}" != "${checked_out}" ]]; then
  echo "::error::main on GitHub is at ${remote_main}, not at the checked-out ${checked_out}. Nothing was published; run the release again from main."
  exit 1
fi

if [[ -n "$(git ls-remote --tags origin "refs/tags/${TAG}")" ]]; then
  echo "::error::Tag ${TAG} already exists on GitHub. Nothing was published."
  exit 1
fi

if ! git push --dry-run origin HEAD:refs/heads/main; then
  echo "::error::The release can't push to main with these credentials. Nothing was published."
  exit 1
fi
