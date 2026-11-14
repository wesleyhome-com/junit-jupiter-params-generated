# Changelog

Notable changes to junit-jupiter-params-generated. Each pull request adds its entry under
`[Unreleased]`; the release workflow renames that section to the released version and uses it as the
GitHub release notes. Pull requests are also listed by label in each GitHub release.

Sections, when they have entries: **Upgrade notes** (anything that can break or change an existing
build, with what to do about it), **Added**, **Changed**, **Fixed**, **Build and docs**.

## [Unreleased]

### Build and docs

- The release workflow creates the GitHub release. Its notes are this changelog's section for the
  version, followed by the release's pull requests grouped by label.
- `CHANGELOG.md` records each release's changes, starting from 4.0.0.

## [4.1.0] - 2026-11-14

### Upgrade notes

- **The JUnit engine is no longer a transitive dependency.** The published POM brings
  `junit-jupiter-api` at compile scope and `junit-jupiter-params` at runtime only. If your build
  relied on this library for the engine, add it yourself; this is JUnit's standard setup:
  `testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")`.
- **One test method can generate at most 1,000,000 invocations by default.** Above that, the test
  fails before running, with a message naming each parameter and how many values it contributes.
  Raise the limit with the `com.wesleyhome.test.jupiter.max.permutations` configuration parameter.
- **Invocation display names follow `@ParameterizedTest`.** They used to read `[1][0.1]` and now
  read `[1] value=0.1`. That matters if anything matches on display names, for example report
  parsing or CI filters. Use the new `name` attribute to choose the format.
- **The `validation` artifact:** `DateTimeRangeValidator.validate` takes a parse function of type
  `(String) -> T`, and a `null` format means "no format". This only matters if you call that
  artifact directly.

### Added

- **Pairwise generation** (#67): `@GeneratedParametersTest(strategy = GenerationStrategy.PAIRWISE)`
  covers every pair of values instead of every combination.
- **Kotlin value classes as parameters** (#64), from the built-in sources or custom providers. If a
  value class's `init` rejects a generated value, only that invocation fails.
- **`@ValuesFrom`** (#65): take a parameter's values from a function that returns a `List`,
  `Sequence` or array.
- **`MIN` and `MAX` keywords** for `LocalDate`, `LocalDateTime`, `LocalTime` and `Instant` value and
  range sources (#66). The default date formats also accept extended and negative years.
- **Filters:** a `<test>_filter` function, or functions listed in `filters = [...]`, skip invalid
  combinations before they run.
- **`@WithNull`:** adds a `null` case explicitly, from Kotlin or Java.
- **Display name patterns** with the `name` attribute: `{index}`, `{arguments}`,
  `{argumentsWithNames}`, `{displayName}`, `{0}`, `{1}`, ….
- **Report entries:** a per-test summary of what was generated, and each invocation's values with
  `com.wesleyhome.test.jupiter.report.values=true`.
- **A fixed clock** for offset-based random date and time sources:
  `com.wesleyhome.test.jupiter.clock.fixed.at`.
- **Public provider base classes:** `AbstractParameterDataProvider` and
  `AbstractAnnotatedParameterDataProvider`.
- **Compile-time checks of date and time ranges** in the annotation processor.

### Fixed

- `@InstantRangeSource` failed on every use (#72).
- `@LocalTimeSource` and `@LocalTimeRangeSource` failed on every use (#74), and a `LocalTime` range
  that stepped past midnight never ended (#73).
- Numeric ranges are computed by index: `0.0..1.0` by `0.1` gives exactly `0.1` … `1.0`, and
  `@LongRangeSource` is no longer truncated above 2^53.
- A range whose `min` equals its `max` is accepted.
- Parameters from other resolvers (`TestInfo`, `@TempDir`, Mockito, Spring) work alongside generated
  ones, and constructor and lifecycle parameters are left to their own resolvers.
- A test with no generated parameters runs once instead of failing the container.
- The provider registry is safe under parallel execution.
- Date ranges ending at `LocalDate.MAX`, `LocalDateTime.MAX` or `Instant.MAX` stop there instead of
  overflowing.

### Build and docs

- Kotlin 2.4.21, JUnit 6.1.3, KSP 2.3.14, Dokka 2.3.0 and Gradle 9.9.1.
- The docs site keeps every release, defaults to the latest one, and has a version selector.
- Pull requests are built and tested before merging; releases are pushed by a deploy key.

## [4.0.0] - 2026-05-30

### Upgrade notes

- **Requires JUnit 6** (6.1.0); 3.x was built against JUnit 5.11.
- **`@RandomInstantSource` is seeded by default**, so it generates the same values on every run.

### Build and docs

- Kotlin 2.3.21.
- Published through the Maven Central Portal.
- Versioned documentation at https://junit-params.wesleyhome.com/.

## Earlier versions

See the [3.0.0 release](https://github.com/wesleyhome-com/junit-jupiter-params-generated/releases/tag/junit-jupiter-params-generated-3.0.0)
and the repository's tags.

[Unreleased]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/v4.1.0...HEAD
[4.1.0]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/v4.0.0...v4.1.0
[4.0.0]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/junit-jupiter-params-generated-3.0.0...v4.0.0
