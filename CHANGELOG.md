# Changelog

Notable changes to junit-jupiter-params-generated. Each pull request adds its entry under
`[Unreleased]`; the release workflow renames that section to the released version and uses it as the
GitHub release notes. Pull requests are also listed by label in each GitHub release.

Sections, when they have entries: **Upgrade notes** (anything that can break or change an existing
build, with what to do about it), **Added**, **Changed**, **Fixed**, **Build and docs**.

## [Unreleased]

### Build and docs

- The release workflow won't release a version whose milestone, open or closed, still has open
  issues or pull requests, and names each one (#176). It also stops when no milestone has the
  version as its title, which catches a mistyped version. Pre-releases have no milestone and skip
  the check. Once the GitHub release is created, the workflow closes the milestone. The check and
  the closing run in jobs of their own, so the release job's token still has no access to issues.
  Pull request and `main` builds test the script that does both.

## [4.1.1] - 2026-11-24

### Upgrade notes

- **A lambda or local function inside a suffixed `<test>_filter_<name>` filter is no longer
  applied as a filter of its own.** The compiler names them after the filter
  (`prices_filter_small$lambda$0`, `prices_filter_small$isSmall`). If their own parameters happened
  to match generated parameters, they were silently applied as extra filters, so the test ran fewer
  combinations than its filter accepts. It now runs all of them, which can mean more invocations
  than before. If the test relied on that extra filtering, write the rule as a filter of its own.
  Filters named plain `<test>_filter` were never affected.
- **Conventional filters that were silently ignored now apply** (#121), so those tests run fewer
  combinations, and fail if the filter is wrong or can't be called. A `<test>_filter` is now found:
  - in a named companion object (`companion object Rules`) without `@JvmStatic`;
  - when it's `internal` and named plain `<test>_filter` (suffixed ones already applied);
  - in a subclass, for a test inherited from a base class;
  - in an interface that the test class or one of its superclasses implements, for any test of that
    name in the class, not only the interface's own default tests;
  - in an enclosing class, for a test in a `@Nested` class, unless the enclosing class or a class
    between them has a test of that name of its own. The filter then stays that test's, as before.
    An instance `<test>_filter` in an enclosing class can't be called, so it now fails the test,
    as one in the test class always has.
- **A function the lookup used to skip can now take precedence** (#121). A filter or `@ValuesFrom`
  function in a named companion object, or an `internal` one, was invisible, so a function of the
  same name in a superclass was used instead. The nearer class's own function is used now. If the
  superclass's was the one you meant, rename one of them. Within one class, the function chosen
  before is still chosen: an `internal` overload doesn't displace it.
- **A subclass's `internal` conventional filter now replaces a base class's filter of the same
  name** (#121). With a public `prices_filter_small` in the base class and an `internal` one in the
  subclass, both used to apply, because the subclass's was only known by its compiled name. Now they
  share one name and only the subclass's applies, so the base class's rule silently stops filtering.
  Give them different names to keep both.
- **`internal` filters are reported by their declared name** (#121). The `generated.filters` report
  entry and the "rejected by" failure message used to show the compiled name, such as
  `prices_filter_small$my_module`, and now show `prices_filter_small`. Update anything that matches
  on the old name. Naming an `internal` function by its compiled name in `filters = [...]` or
  `@ValuesFrom` still works, and a filter reached by both names is listed once.
- **A filter with several overloads that could each filter the test now fails it** (#122). With
  `startRule(start: Int)` and `startRule(start: Int, end: Int)` in one companion object, or a single
  `@JvmOverloads` filter, a test generating both `start` and `end` used whichever overload reflection
  listed first. The JVM doesn't specify that order, and it changes between builds and even between
  runs of the same build, so the same test could run a different set of combinations each time it
  ran. The test now fails with
  `Filter [startRule] is overloaded, and 2 overloads can filter this test: startRule(int),
  startRule(int, int)`. Give the overloads different names, or drop `@JvmOverloads`. Where the
  choice never depended on that order, the same overload is still used: a public one before an
  `internal` one, then a static or `@JvmStatic` one before one declared only in a companion object.
- **A filter that names a different parameter than its position gave it now gets the one it
  names** (#120). For a Java test compiled without `-parameters`, a filter with no `-parameters`
  names either was bound by position: each of its parameters took the test parameter in the same
  place, whatever either was called. That covers a Java filter compiled the same way, and a Kotlin
  filter compiled without `javaParameters`. Position still decides, unless one of the filter's
  names belongs to a generated parameter in another place. `endNotOne_filter(int end)` on
  `test(int start, int end)` filtered on `start`, and passed; it now filters on `end`. The test runs
  the combinations it was written for, which can mean different invocations, and failures the old
  binding hid. Once one name decides, they all must be the test's. So `(int end, int other)` now
  fails with `declares [other], which is not a generated parameter of the test`, and
  `(int a, int start)`, which ran by position, fails with `declares [a]`: rename the parameter. A
  filter whose names agree with their positions binds as before. So does one with names the test
  doesn't have, such as `(int a, int b)` or `(int a, int end)`, which also logs a warning naming
  them. For Kotlin tests and tests compiled with `-parameters`, only filters that used to fail are
  affected. With overloads, this can now fail a test in two ways. An overload that now binds by
  name can leave two that filter it: for `mixed(int start, String label)`, `rule(String label)`
  used to take `start` and couldn't, so `rule(int start)` was used. Now both can, and the test
  fails with `Filter [rule] is overloaded, and 2 overloads can filter this test`; give them
  different names. And an overload that bound by position, but whose names now don't all match,
  fails the test as it would alone, rather than giving way to another overload.
- **A Kotlin filter parameter that isn't nullable can't take a generated null** (#123). Kotlin
  checks the parameters of every function but a `private` one for null, so such a filter failed
  already, with a `NullPointerException` partway through the run. Compiled with
  `-Xno-param-assertions`, it has no such check and took the null in a parameter declared not to
  hold one. It now fails before anything runs. Declare its parameter nullable (`label: String?`).
  This doesn't affect a `private` filter, which has no check, or a filter whose parameter another
  of the test's filters takes and can hold the null: those run as before.
- **A filter over a value class whose box fits its compiled type now gets the value inside**
  (#115). `AnyBox(val value: Any)`, `Box<T>(val value: T)` and the standard library's
  `Result<T>`, a value class over `Any?`, compile to `Object`, and
  `Label(val text: CharSequence) : CharSequence by text` to `CharSequence`. So the filter used to
  be given the box where its code expected the value: `box.value` was the box itself, and
  `result.isSuccess` was true for a failure too. A rule like that, or one that only compared the
  value, such as `box.value != "y"`, quietly let through or turned away the wrong combinations,
  and the test passed running them. With `box: AnyBox?`, `box == null || box.value == "x"` ran the
  null alone. The filter now gets the value its Kotlin declaration says, so such a test runs the
  combinations its filter was written for. That can mean different invocations, or a failure the
  old value hid. Display names and generated values don't change.
- **On JUnit 6.0, a test method taking a value class whose box fits its compiled type now gets
  the value inside** (#168), as it already did on JUnit 6.1. These are the value classes in the
  note above, `AnyBox`, `Box<T>`, `Result<T>` and `Label`, and a value class over one of them.
  JUnit 6.0 calls the test with `Method.invoke`, so the test was given the box where its code
  expected the value: `box.value` was the box itself, `box.value` of a `Box<Int>` threw a
  `ClassCastException`, and `result.isSuccess` was true for a failure. A test body that passed with
  the box can now fail, such as one that calls `result.getOrThrow()` and ignores the result, or
  compares `box.value` with a box, as `assertEquals(AnyBox("x"), box.value)` does. The same goes
  for such a box from another extension's parameter resolver. On any JUnit version, an
  `InvocationInterceptor` of your own that reads the test's arguments now sees a generated value
  of these classes unboxed, as the test gets it. Display names, report entries, filters and
  invocation counts don't change.

### Added

- **Kotlin 2.2 projects, such as Spring Boot 4.0's, can use the library** (#170). It's still
  compiled by Kotlin 2.4, but its classes are built for Kotlin 2.2, so a 2.2 compiler reads them.
  With 4.1.0 it failed: `The actual metadata version is 2.4.0, but the compiler version 2.2.0 can
  read versions up to 2.3.0`. Kotlin 2.3 projects, such as Spring Boot 4.1's, already compiled
  against 4.1.0, since a 2.3 compiler reads 2.4 metadata. They're now inside the supported range.
  The POMs still ask for `kotlin-stdlib` and `kotlin-reflect` 2.4.21 at run time, as before. A
  Maven project on Kotlin 2.2 without Spring Boot's dependency management declares its own
  `kotlin-reflect`, as the README describes. The annotation processor needs KSP2, from
  2.2.21-2.0.4. In the class files, only what the compiler records for Kotlin changes: the Kotlin
  metadata, which is 2.2's and so no longer repeats the classes' annotations as 2.4's does, and one
  coroutine's debug metadata.

### Fixed

- **Conventional filters no longer pick up methods that aren't filters** (#124). A test of its own
  named `<test>_filter_<something>` (`@Test`, `@ParameterizedTest`, `@GeneratedParametersTest` or any
  other JUnit test annotation), or a method the compiler generates for a `<test>_filter_<name>`
  filter - for a lambda or local function inside it, or for a default argument - used to be taken as
  a filter of `<test>`.
  That failed the test with errors such as `Filter [price_filter_applies] must be static…` or
  `Filter [prices_filter_small$lambda$0] declares [$start]…`.
- **More places for filters and `@ValuesFrom` functions** (#121): a named companion object, an
  `internal` function, an interface the test class implements directly or through a superclass,
  and for a `@Nested` class the classes enclosing it. Naming one in `filters = [...]` or
  `@ValuesFrom` used to fail with `No filter named […]` or `no function named […]`. Conventional
  filters are now looked for on the class running the test rather than the one declaring it, so
  they also apply to inherited tests and to an interface's default tests.
- **Overloaded filters and `@ValuesFrom` functions are chosen the same way every time** (#122).
  Which overload was used depended on the order reflection listed methods in, so a test could pass
  in one run and fail in the next with `Filter [...] declares [label], which is not a generated
  parameter of the test` or `[pts] must take no arguments, but takes 1`. A filter now uses the
  overload whose parameters all match generated parameters, even beside a `@JvmStatic` or public
  overload that doesn't match, which used to be chosen every time and fail the test. `@ValuesFrom`
  uses the overload that takes no arguments, and the annotation processor checks that overload too,
  so declaring `pts(n: Int)` before `pts()` no longer fails the build. An override that narrows the
  return type, such as `override fun pts(): List<Int>` for an interface's `fun pts(): T`, is used
  rather than the bridge the compiler adds for it, which sometimes failed with `[pts] must return a
  List, Sequence or array, but returns Any`. If no overload of a filter can filter the test, the
  failure lists what's wrong with each one.
- **Filters work without `javaParameters` or `-parameters`** (#120). Kotlin filters failed with
  `Filter [...] declares [arg0], which is not a generated parameter of the test` unless compiled
  with `javaParameters`, which isn't Kotlin's default. They now bind by the names in Kotlin's
  metadata, `@JvmSynthetic` ones included. Java filters read their names from the debug
  information, so they also bind for a test whose first parameter is resolved by another
  extension, such as `TestInfo`. Messages about a filter's parameters, and `@ValuesFrom` messages
  for Java tests, name parameters as declared rather than `arg0`. A filter that still binds to a
  Java test by position, whether it's in Java or in Kotlin compiled without `javaParameters`, logs
  a warning, once for each test that uses it, when the class files record no names or the
  filter's names aren't the test's. Display names and report entries are unchanged: for Java they
  still need `-parameters` to show declared names.
- **A filter that throws is named, with the combination it threw on** (#123). The test used to
  fail with a bare `InvocationTargetException: null`. It now fails with an `InvalidFilterException`
  such as `Filter [ordered_filter] on [start=2, end=1] threw IllegalStateException: boom`, with the
  filter's exception as its cause, as a throwing `@ValuesFrom` function already did. An
  `OutOfMemoryError` is now thrown as it is, so JUnit stops the run, as for any `OutOfMemoryError`.
- **A Kotlin filter parameter that isn't nullable is rejected for a parameter that generates
  null** (#123), from `@WithNull` or a nullable type, with the message a primitive one gets:
  `Filter [label_filter] declares [label] as String, which cannot hold the null this parameter
  generates`. It's rejected before anything runs where Kotlin's own null check would fail the test
  once the null reached the filter, with a `NullPointerException` from inside it: in a filter that
  isn't `private`, unless another filter of the test takes the same parameter and can hold the
  null. A value class is judged by its Kotlin type, so `name: Name` is rejected and `name: Name?`
  accepted. One over a nullable type, such as `Nickname(val value: String?)`, still takes the null
  as `Nickname(null)`. A `private` filter and a Java filter take the null as before. Any filter
  that then throws on the null is reported as a throwing filter.
- **A filter over a value class wrapping `Any` or a type parameter, or implementing the interface
  it wraps, gets the value its Kotlin declaration says** (#115). That's the value the test method
  gets too, on every JUnit version (#168). Given the box, a filter declaring `box: Box<Int>`
  failed the test with `InvocationTargetException: null`, caused by a `ClassCastException`, and
  one declaring `box: AnyBox` rejected everything:
  `All 2 generated combinations were rejected by [anyBox_filter]`. Values are now unboxed as far
  as the filter's Kotlin parameter types say. A filter with a context parameter is still given the
  box, as a Java filter is: its Kotlin parameters don't line up with its compiled ones. A filter
  declaring `amount: Money` still takes a generated `Long`, both being a `long` in the signature.
- **A source for the innermost type generates a value class over another value class** (#115).
  `@LongSource` on `wallet: Wallet`, for `Wallet(val money: Money)`, failed with `Unable to find a
  suitable data provider for parameter [wallet]`, though `@ValuesFrom` worked. A value that an
  inner value class's `init` rejects fails only its own invocation, naming that class.
- **A filter with a context parameter and a value-class parameter fails with a message** (#115),
  rather than `IndexOutOfBoundsException: Index 1 out of bounds for length 1`. Its Kotlin
  parameters don't line up with its compiled ones, so it's judged by its compiled signature alone:
  `Filter [rule] declares [amount] as long, but the generated parameter is Money`.
- **A test method taking some combinations of value classes no longer fails on JUnit 6.1**
  (#168). JUnit 6.1 has kotlin-reflect call the test when an argument is a value class's box, and
  kotlin-reflect takes every value-class argument to be boxed. So a test taking `Money`, passed
  unboxed as a `long`, beside a box such as an `AnyBox`, a `Money?` or a `Box<Money>`, failed with
  `object is not an instance of declaring class`. One taking `Wallet?`, for
  `Wallet(val money: Money)`, failed with `argument type mismatch` for every value but null. When
  an argument is a box, the test is now called with `Method.invoke`, as JUnit 6.0 calls it, given
  each value as its compiled signature takes it. Otherwise JUnit calls it, as before, and so it
  does a `suspend` test. On JUnit 6.1, a test that threw with a box among its arguments was
  reported as an `InvocationTargetException`, with its exception as the cause. So
  `assumeTrue(false)` failed the test rather than aborting it, and a
  `TestExecutionExceptionHandler` was given the `InvocationTargetException` rather than the test's
  exception. The test is now reported as what it threw: aborted for a failed assumption, and
  handed to the handler as it is.
- **A value class over a nullable type reaches the test method holding null** (#168), when it comes
  boxed from `@ValuesFrom` or a custom provider. `Nickname(null)`, for
  `Nickname(val value: String?)`, failed on every JUnit version with `ParameterResolver [...]
  resolved a value of type [...Nickname] for parameter [java.lang.String nickname] in method [...],
  but a value assignment compatible with [java.lang.String] is required`. So did
  `OuterMoney(null)`, for `OuterMoney(val inner: Money?)`. The null `@WithNull` adds already
  reached a `Nickname` test as `Nickname(null)`. On JUnit 6.1, a value class over a nullable value
  class failed holding a value too, such as `OuterMoney(Money(1))`, with `object is not an instance
  of declaring class`. These now run.

### Build and docs

- Builds enforce minimum line and branch test coverage for `extension`, `validation` and
  `annotation-processor` (#145). `./gradlew check` now runs `jacocoTestCoverageVerification`, which
  failed before checking anything. Each module's minimums sit just below the coverage when the
  gate was added, so a pull request that lowers coverage past them fails its `build` check.
- The release workflow creates the GitHub release. Its notes are this changelog's section for the
  version, followed by the release's pull requests grouped by label.
- `CHANGELOG.md` records each release's changes, starting from 4.0.0.
- Pull requests must pass a `changelog` check: an entry under `[Unreleased]`, a release-note label,
  and an Upgrade note for anything labeled `breaking`.
- `CLAUDE.md` gives Claude Code sessions the project's contribution rules.
- Releases build on JDK 17, as pull requests and `main` do, and run every project's build and tests
  before publishing. Before building, and again before publishing, they check that `main` hasn't
  moved, that the release tag doesn't exist yet, and that the deploy key can still reach the
  repository for pushing. The release plugin's tasks are listed by `./gradlew tasks`.
- JaCoCo 0.8.16 measures the coverage gate.
- `./gradlew test -PjunitVersion=6.0.3` runs every project's tests on another JUnit version, such
  as 6.0.3, the oldest this library supports (#168). Only the tests' runtime classpath changes.
- `./gradlew test -PkotlinRuntime=2.2.21` runs every project's tests on another `kotlin-stdlib` and
  `kotlin-reflect`, such as 2.2.21, the oldest this library supports (#170). Tests of context
  parameters, which kotlin-reflect reads from 2.4 on, are skipped on an older one.
- Pull request and `main` builds also build and test Kotlin 2.2.21 projects against the library
  (#170): in Gradle, with and without Spring Boot 4.0.9's dependency management and with the
  annotation processor under KSP 2.2.21-2.0.4, and in Maven. They also check that every published
  class carries Kotlin 2.2 metadata. `bash .github/scripts/consumer-smoke-test.sh` runs them
  locally.
- A `floor-versions` check runs every test on `kotlin-stdlib` and `kotlin-reflect` 2.2.21 and JUnit
  6.0.3, beside the `build` check (#170).

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

[Unreleased]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/v4.1.1...HEAD
[4.1.1]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/v4.1.0...v4.1.1
[4.1.0]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/v4.0.0...v4.1.0
[4.0.0]: https://github.com/wesleyhome-com/junit-jupiter-params-generated/compare/junit-jupiter-params-generated-3.0.0...v4.0.0
