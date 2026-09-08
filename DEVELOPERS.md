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

## Measuring Generation Performance

There is no benchmark module and no timing assertion in CI - timing assertions on shared runners are
a reliable source of flaky builds. Instead, this is the procedure that produced the numbers in the
performance investigation, so they can be reproduced or challenged.

### Harness

Add a temporary probe under `extension/src/test/kotlin/perfprobe/`, with empty test bodies so the
measurement is of generation rather than of the test:

```kotlin
class OneParamTest {
    @GeneratedParametersTest(name = "{index}")
    fun oneParam(@IntRangeSource(min = 1, max = 100000) value: Int) {}
}

class BaselineTest {                       // Jupiter's own path, for comparison
    @ParameterizedTest(name = "{index}")
    @MethodSource("values")
    fun baseline(value: Int) {}
    companion object {
        @JvmStatic fun values(): Stream<Int> = IntStream.rangeClosed(1, 100000).boxed()
    }
}

class OverheadTest {                       // fixed Gradle and JVM cost, to subtract
    @Test fun single() {}
}
```

and a temporary block in `extension/build.gradle.kts`:

```kotlin
tasks.test {
    maxHeapSize = "2g"
    reports.junitXml.required.set(false)
    reports.html.required.set(false)
    extensions.configure<org.gradle.testing.jacoco.plugins.JacocoTaskExtension> { isEnabled = false }
    jvmArgs("-Xlog:gc:file=<absolute path>/gc.log")
}
```

Four details matter, each of which distorted a measurement before it was pinned down:

- **Disable the reports.** Writing 100,000 result entries swamps what is being measured.
- **Disable the JaCoCo agent.** It instruments this project's classes but not JUnit's, so it does
  not affect the two paths equally. It changes the absolute numbers substantially.
- **Subtract the overhead.** A trivial single-test run costs 2.5 s or so of Gradle and JVM startup,
  which is most of a short measurement.
- **Take the best of three.** Run-to-run noise is around 10 microseconds per invocation, which is
  large enough to invert an ordering. A single run cannot distinguish a 10% effect.

Give each variant its own `--tests` filter so Gradle actually re-runs the task, and use an absolute
Windows-style path for `-Xlog` - a POSIX path from Git Bash will stop the JVM from starting.

### Reading the results

Per-invocation cost is `(total - overhead) / invocations`. GC collection count from the log
(`grep -c "Pause Young\|Pause Full"`) is a good proxy for allocation rate.

For memory, the number that matters is the **live heap after each collection**, not the peak:

```bash
grep -oE "[0-9]+M->[0-9]+M\([0-9]+M\)" gc.log | sed -E 's/.*->([0-9]+)M\(.*/\1/'
```

A monotonic climb is retention. Run the baseline the same way before concluding the retention is
this library's - it mostly is not. `org.junit.platform.launcher.TestPlan` holds a `TestIdentifier`
per invocation for the life of the run, roughly 900 bytes each, and every engine pays it.

### For a throughput question, profile rather than guess

Allocation and execution profiles come from `-XX:StartFlightRecording=filename=...,settings=profile`,
read back with `jfr print --events jdk.ObjectAllocationSample` and `jdk.ExecutionSample`. Note that a
recording started at JVM launch may collect very few execution samples during the window that
matters; async-profiler is the better tool if the question is where CPU time goes.

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
