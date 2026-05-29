#!/usr/bin/env bash
set -euo pipefail

MODE="${1:-latest}"
DOCS_DIR="${2:-docs}"
EXISTING_DIR="${3:-gh-pages}"
OUT_DIR="${4:-site}"
RELEASE_VERSION="${5:-}"

if [[ ! -d "${DOCS_DIR}" ]]; then
  echo "Documentation directory '${DOCS_DIR}' was not found."
  exit 1
fi

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

if [[ -d "${EXISTING_DIR}" ]]; then
  if [[ -d "${EXISTING_DIR}/v" ]]; then
    mkdir -p "${OUT_DIR}/v"
    cp -R "${EXISTING_DIR}/v/." "${OUT_DIR}/v/"
  fi
  if [[ -f "${EXISTING_DIR}/CNAME" ]]; then
    cp "${EXISTING_DIR}/CNAME" "${OUT_DIR}/CNAME"
  fi
fi

case "${MODE}" in
  latest)
    rm -rf "${OUT_DIR}/latest"
    mkdir -p "${OUT_DIR}/latest"
    cp -R "${DOCS_DIR}/." "${OUT_DIR}/latest/"
    cp -R "${DOCS_DIR}/." "${OUT_DIR}/"
    ;;
  release)
    if [[ -z "${RELEASE_VERSION}" ]]; then
      echo "Release mode requires a version argument."
      exit 1
    fi
    rm -rf "${OUT_DIR}/v/${RELEASE_VERSION}"
    mkdir -p "${OUT_DIR}/v/${RELEASE_VERSION}"
    cp -R "${DOCS_DIR}/." "${OUT_DIR}/v/${RELEASE_VERSION}/"
    if [[ ! -d "${OUT_DIR}/latest" ]]; then
      mkdir -p "${OUT_DIR}/latest"
      cp -R "${DOCS_DIR}/." "${OUT_DIR}/latest/"
      cp -R "${DOCS_DIR}/." "${OUT_DIR}/"
    fi
    ;;
  *)
    echo "Unknown mode '${MODE}'. Expected 'latest' or 'release'."
    exit 1
    ;;
esac

touch "${OUT_DIR}/.nojekyll"

mapfile -t RELEASE_DIRS < <(find "${OUT_DIR}/v" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' 2>/dev/null | sort -Vr || true)

{
  echo "{"
  echo "  \"latest\": \"latest/\","
  echo "  \"versions\": ["
  echo "    {\"label\": \"latest\", \"path\": \"latest/\"}"
  for v in "${RELEASE_DIRS[@]}"; do
    echo "    ,{\"label\": \"${v}\", \"path\": \"v/${v}/\"}"
  done
  echo "  ]"
  echo "}"
} > "${OUT_DIR}/versions.json"
