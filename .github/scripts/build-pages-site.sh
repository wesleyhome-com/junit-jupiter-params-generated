#!/usr/bin/env bash
# Builds the Pages site from the stored site plus one new set of docs.
#
# The stored site (the gh-pages branch) keeps every release under v/<version>/ and the main branch
# under snapshot/. The root and latest/ are derived on every build: they are copies of the newest
# GA release (a plain X.Y.Z version), or of the snapshot until a GA release exists.
#
# When a selector script is given, it replaces the copy in every stored version, so pages built from
# older tags get the current version selector too.
#
# Usage: build-pages-site.sh <snapshot|release> <docs dir> <stored site dir> <output dir> [version] [selector js]
set -euo pipefail

MODE="${1:?mode (snapshot or release) is required}"
DOCS_DIR="${2:-docs}"
STORED_DIR="${3:-gh-pages}"
OUT_DIR="${4:-site}"
RELEASE_VERSION="${5:-}"
SELECTOR_JS="${6:-}"

if [[ ! -d "${DOCS_DIR}" ]]; then
  echo "Documentation directory '${DOCS_DIR}' was not found."
  exit 1
fi

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

for kept in v snapshot CNAME; do
  if [[ -e "${STORED_DIR}/${kept}" ]]; then
    cp -R "${STORED_DIR}/${kept}" "${OUT_DIR}/${kept}"
  fi
done

case "${MODE}" in
  snapshot)
    rm -rf "${OUT_DIR}/snapshot"
    cp -R "${DOCS_DIR}/." "${OUT_DIR}/snapshot/"
    ;;
  release)
    if [[ -z "${RELEASE_VERSION}" ]]; then
      echo "Release mode requires a version argument."
      exit 1
    fi
    rm -rf "${OUT_DIR}/v/${RELEASE_VERSION}"
    mkdir -p "${OUT_DIR}/v"
    cp -R "${DOCS_DIR}/." "${OUT_DIR}/v/${RELEASE_VERSION}/"
    ;;
  *)
    echo "Unknown mode '${MODE}'. Expected 'snapshot' or 'release'."
    exit 1
    ;;
esac

if [[ -n "${SELECTOR_JS}" ]]; then
  for stored in "${OUT_DIR}"/v/*/ "${OUT_DIR}/snapshot/"; do
    if [[ -f "${stored}images/version-selector.js" ]]; then
      cp "${SELECTOR_JS}" "${stored}images/version-selector.js"
    fi
  done
fi

# "~" sorts before everything in version order, which puts 4.1.0-RC1 below 4.1.0.
mapfile -t RELEASES < <(find "${OUT_DIR}/v" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' 2>/dev/null \
  | sed 's/-/~/' | sort -Vr | sed 's/~/-/' || true)

LATEST_GA=""
for v in "${RELEASES[@]}"; do
  if [[ "${v}" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    LATEST_GA="${v}"
    break
  fi
done

if [[ -n "${LATEST_GA}" ]]; then
  DEFAULT_PATH="v/${LATEST_GA}/"
elif [[ -d "${OUT_DIR}/snapshot" ]]; then
  DEFAULT_PATH="snapshot/"
else
  echo "Nothing to publish: no releases and no snapshot."
  exit 1
fi

cp -R "${OUT_DIR}/${DEFAULT_PATH}." "${OUT_DIR}/latest/"
cp -R "${OUT_DIR}/${DEFAULT_PATH}." "${OUT_DIR}/"

touch "${OUT_DIR}/.nojekyll"

{
  echo "{"
  echo "  \"latest\": \"${DEFAULT_PATH}\","
  echo "  \"versions\": ["
  separator=" "
  for v in "${RELEASES[@]}"; do
    label="${v}"
    if [[ "${v}" == "${LATEST_GA}" ]]; then
      label="${v} (latest)"
    fi
    echo "   ${separator}{\"label\": \"${label}\", \"path\": \"v/${v}/\"}"
    separator=","
  done
  if [[ -d "${OUT_DIR}/snapshot" ]]; then
    echo "   ${separator}{\"label\": \"snapshot\", \"path\": \"snapshot/\"}"
  fi
  echo "  ]"
  echo "}"
} > "${OUT_DIR}/versions.json"

echo "Published ${MODE}${RELEASE_VERSION:+ ${RELEASE_VERSION}}; default docs: ${DEFAULT_PATH}"
