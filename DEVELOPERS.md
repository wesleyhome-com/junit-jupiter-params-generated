# Developer Guide

## Purpose

This repository provides `junit-jupiter-params-generated`, a JUnit Jupiter extension for generated parameterized test data with optional compile-time validation.

## Prerequisites

- JDK 17
- Gradle Wrapper (included)
- GitHub Actions permissions for release/docs publishing (maintainers)

## Repository Layout

- `extension`: Core runtime extension, annotations, parameter generation, providers.
- `validation`: Shared validation logic used by runtime and processor.
- `annotation-processor`: KSP-based compile-time checks.
- `examples`: Usage examples and sample wiring.
- `buildSrc`: Shared Gradle convention plugins.

## Common Commands

Run from repository root.

- Full build:
  - `./gradlew build`
- Run all tests:
  - `./gradlew test`
- Run tests for extension module only:
  - `./gradlew :junit-jupiter-params-generated:test`
- Run one test class:
  - `./gradlew :junit-jupiter-params-generated:test --tests com.wesleyhome.test.jupiter.provider.datetime.RandomInstanceSourceDataProviderTest`
- Generate docs:
  - `./gradlew dokkaGenerate`

## Development Workflow

1. Make code changes in the relevant module(s).
2. Add/adjust tests in `extension/src/test` (and `annotation-processor` tests when applicable).
3. Run targeted tests first, then full module tests.
4. Update README/KDoc when behavior or API changes.
5. Keep commits focused (feature, test, docs).

## Testing Guidance

- Prefer behavior-focused tests around tuple generation and invocation output.
- Cover both Kotlin and Java interop paths for API-facing features.
- For deterministic behavior claims, add explicit repeatability assertions.

## Release and Publishing Overview

Main release/publish behavior is driven by GitHub workflows under `.github/workflows`.

- `build-main.yml`:
  - builds/tests,
  - publishes SNAPSHOT artifacts when `version` ends with `-SNAPSHOT`,
  - generates docs,
  - deploys latest docs to GitHub Pages.
- `release.yml`:
  - manual release workflow (`workflow_dispatch`),
  - runs `net.researchgate.release`,
  - publishes non-SNAPSHOT release artifacts to Maven Central.
- `build-branches.yml`:
  - verifies non-main branches.

## Documentation Publishing and Versioning

Docs are published to GitHub Pages with two lanes:

- `main` branch pushes update:
  - `/latest/` (latest docs),
  - `/` (root copy of latest docs).
- tag pushes or manual dispatch publish versioned docs:
  - `/v/<version>/`.

Supporting files:

- `.github/workflows/build-main.yml`
- `.github/workflows/docs-version-release.yml`
- `.github/scripts/build-pages-site.sh`
- `dokka-assets/version-selector.js`

Version metadata is generated as `versions.json` at site root and used by the Dokka version selector asset.

## Backfilling Older Docs Versions

To backfill a prior release (example: `3.0.0`):

1. Checkout the release tag/commit.
2. Run `Publish Versioned Docs` (`workflow_dispatch`) with `version=3.0.0`.
3. The workflow publishes docs under `/v/3.0.0/` without removing existing version folders.

## Notes and Known Gotchas

- Keep line endings consistent via `.gitattributes`.
- If docs output looks stale, ensure the generated site has been deployed (README changes alone do not update Pages until workflow runs).
