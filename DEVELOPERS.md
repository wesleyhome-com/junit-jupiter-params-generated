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
- Check test coverage against the gate (see [Coverage Gate](#coverage-gate)):
  - `./gradlew jacocoTestCoverageVerification`
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

## Coverage Gate

`./gradlew check`, and so `build` and the required `build` pull request check, runs
`jacocoTestCoverageVerification` in `extension`, `validation` and `annotation-processor`. Each of
those modules sets a minimum line and branch coverage for the module as a whole, in its
`build.gradle.kts`. A change that takes a module below either minimum fails the build:

```
Rule violated for bundle annotation-processor: branches covered ratio is 0.66, but expected minimum is 0.67
```

The minimums started at the coverage when the gate was added, rounded down to the percent (a
percent lower in the two small modules), so coverage can stay where it is or rise:

| Module                 | Lines | Branches | Headroom                                      |
|------------------------|-------|----------|-----------------------------------------------|
| `extension`            | 0.98  | 0.92     | only the rounding                             |
| `validation`           | 0.93  | 0.83     | a percent lower: about 2 lines and 1 branch   |
| `annotation-processor` | 0.86  | 0.67     | a percent lower: about 2 lines and 2 branches |

`validation` and `annotation-processor` are small enough that a single uncovered line or branch
moves their coverage by half a percent or more, so they keep a percent of headroom.

The JaCoCo version is pinned as `jacoco` in `gradle/libs.versions.toml`, because a different
JaCoCo can measure unchanged code differently. Renovate proposes its bumps in a pull request of
their own, and the gate judges that pull request like any other.

- **Run it:** `./gradlew jacocoTestCoverageVerification`, or one module with, for example,
  `./gradlew :annotation-processor:jacocoTestCoverageVerification`. It runs the tests it needs.
- **See what isn't covered:** each test run writes `<module>/build/reports/jacoco/test/html/index.html`,
  which marks missed lines and branches in the source.
- **When it fails,** add tests for the code the change added or left uncovered. Lowering a minimum
  is a decision for the pull request to explain, not a fix.
- **Raise a minimum** when coverage rises, keeping the same headroom: read the module's Total row
  in the HTML report, round down to the percent (and take a percent off for `validation` and
  `annotation-processor`), and change the `minimum` values in that module's `build.gradle.kts` in
  the same pull request, so the gain is kept.

`validation` has no tests of its own. Its report and gate read the JaCoCo data from the
`extension` and `annotation-processor` tests, so checking it runs those tests. `examples` has no
gate: its samples are compiled, and checked by the annotation processor, but never run.

The JaCoCo agent only instruments `com.wesleyhome.*` classes. Instrumenting everything also
instruments the Kotlin compiler that the annotation processor's tests run, which slowed them by
about 40%.

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

- `build-pr.yml`:
  - builds and tests every pull request to `main`, including the [coverage gate](#coverage-gate);
    its `build` check is required to merge.
- `build-main.yml`:
  - builds/tests,
  - publishes SNAPSHOT artifacts when `version` ends with `-SNAPSHOT`,
  - generates docs and publishes them as the snapshot docs (see below).
- `release.yml`:
  - manual release workflow (`workflow_dispatch`),
  - runs `net.researchgate.release`,
  - publishes non-SNAPSHOT release artifacts to Maven Central,
  - pushes the release commits and tag with the `RELEASE_DEPLOY_KEY` deploy key, the only actor
    allowed to push to `main` directly,
  - renames `CHANGELOG.md`'s `[Unreleased]` section to the release version before building, and
    stops the release there if that section is empty (see below),
  - creates the GitHub release: that version's changelog section, then the pull requests since the
    previous GA tag (or the tag just before a pre-release) grouped by label. An `X.Y.Z` version is
    marked Latest, anything else a pre-release. If that last step fails, the artifacts are already
    published; create the release by hand.

## Changelog and Labels

Every pull request does two things for the release notes:

- **Adds an entry to `CHANGELOG.md` under `[Unreleased]`,** in the section it belongs to: Upgrade
  notes, Added, Changed, Fixed, or Build and docs. Write it for someone upgrading, not as a commit
  message. Anything that can break or change an existing build goes under **Upgrade notes** with what
  to do about it.
- **Gets a label:** `enhancement`, `bug`, `build`, `documentation` or `dependencies`, plus `breaking`
  for anything with an upgrade note. `.github/release.yml` groups the release's pull request list by
  these labels. Renovate labels its own pull requests `dependencies`, and those don't need a
  changelog entry. `skip-changelog` leaves a pull request out of the list.

The `changelog` check (`.github/workflows/pr-changelog.yml`) enforces both, and `main` requires it.
It fails a pull request that:

- has none of those labels, or `skip-changelog`;
- leaves `[Unreleased]` unchanged, unless it's labeled `skip-changelog` or `dependencies`;
- is labeled `breaking` but adds nothing under `[Unreleased]`'s **Upgrade notes**.

Adding or removing a label re-runs the check.

At release time the `releaseChangelog` task (in `build.gradle.kts`, run before the release build)
renames `[Unreleased]` to `[<version>] - <date>`, starts a new empty `[Unreleased]` and updates the
compare links. The release plugin's pre-tag commit includes the change, so the tag carries it. An
empty `[Unreleased]` stops the release before anything is built or published; add the missing
entries in a pull request and run it again.

## Documentation Publishing and Versioning

The site at https://junit-params.wesleyhome.com/ is laid out as:

- `/v/<version>/`: docs for each release, published when its tag is pushed.
- `/snapshot/`: docs for `main`, published on every push to `main`.
- `/` and `/latest/`: copies of the newest GA release (a plain `X.Y.Z` version), or of the
  snapshot until a GA release exists.
- `/versions.json`: what the version selector lists, newest release first and the snapshot last.

GitHub Pages deploys a workflow artifact, which replaces the whole site, so the site is also stored
on the `gh-pages` branch. Each publish starts from that branch, adds or replaces its own folder,
recomputes `/`, `/latest/` and `versions.json`, writes the result back to `gh-pages`, and deploys it.
Both workflows share the `pages-site` concurrency group so they never write the branch at once.
GitHub keeps only one pending run per group, so if a snapshot publish replaces a queued release
publish, re-run the release one as described below.

Supporting files:

- `.github/workflows/build-main.yml`
- `.github/workflows/docs-version-release.yml`
- `.github/scripts/build-pages-site.sh`
- `.github/scripts/store-pages-site.sh`
- `dokka-assets/version-selector.js`

## Backfilling Older Docs Versions

To publish or republish a release's docs (example: `4.0.0`), run `Publish Versioned Docs`
(`workflow_dispatch`) from `main` with `version=4.0.0`. It checks out that version's tag (`v4.0.0`,
or `junit-jupiter-params-generated-<version>` for releases before 4.0.0), generates its docs, and
publishes them under `/v/4.0.0/` without touching the other versions.

## Notes and Known Gotchas

- Keep line endings consistent via `.gitattributes`.
- If docs output looks stale, ensure the generated site has been deployed (README changes alone do not update Pages until workflow runs).
