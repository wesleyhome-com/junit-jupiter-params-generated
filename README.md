# JUnit Jupiter Parameterized Test Extension

This project extends JUnit Jupiter parameterized tests with generated parameter values for numbers, date/time types, strings, booleans, and enums.

It is designed to reduce `@MethodSource` boilerplate while preserving type safety and adding compile-time validation for supported annotations.

## Installation

### Runtime Extension

Gradle:

```kotlin
dependencies {
    testImplementation("com.wesleyhome.test:junit-jupiter-params-generated:<latestVersion>")
}
```

Maven:

```xml
<dependency>
    <groupId>com.wesleyhome.test</groupId>
    <artifactId>junit-jupiter-params-generated</artifactId>
    <version>${latestVersion}</version>
    <scope>test</scope>
</dependency>
```

### Annotation Processor (Optional, recommended)

Gradle (KSP):

```kotlin
plugins {
    id("com.google.devtools.ksp") version "<kspVersion>"
}

dependencies {
    ksp("com.wesleyhome.test:annotation-processor:<latestVersion>")
}
```

Maven:

```xml
<dependency>
    <groupId>com.wesleyhome.test</groupId>
    <artifactId>annotation-processor</artifactId>
    <version>${latestVersion}</version>
    <scope>provided</scope>
</dependency>
```

## Compatibility Matrix

| Component | Supported | Verified against |
| --- | --- | --- |
| Java | 17+ | 17 (class file major version 61) |
| Kotlin/JVM | 2.4+ | 2.4.20 |
| JUnit Jupiter | 6.0.3+ | 6.1.3, with the full suite re-run against 6.0.3 |
| Gradle | any version supporting the above | 9.7.1 |
| Maven | `test` scope for the extension | - |

Kotlin 2.4 is a floor rather than a preference: the published classes carry Kotlin metadata v2.4,
and earlier compilers refuse to read it. Consuming this library from a Kotlin 2.3 build fails with
"compiled with an incompatible version of Kotlin".

The optional annotation processor runs under KSP, so compile-time validation applies to Kotlin
sources only. Java sources get the full runtime behaviour, but misconfigured annotations surface
when the test runs rather than when it compiles.

## Getting Started

```kotlin
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import kotlin.test.assertTrue

@GeneratedParametersTest
fun testWithGeneratedParameters(@IntRangeSource(min = 1, max = 10) value: Int) {
    assertTrue(value in 1..10)
}
```

## Core Sources

- Numeric ranges: `@IntRangeSource`, `@LongRangeSource`, `@DoubleRangeSource`, `@FloatRangeSource`
- Numeric explicit values: `@IntSource`, `@LongSource`, `@DoubleSource`, `@FloatSource`
- Date/time ranges: `@InstantRangeSource`, `@LocalDateRangeSource`, `@LocalDateTimeRangeSource`, `@LocalTimeRangeSource`
- Date/time explicit values: `@InstantSource`, `@LocalDateSource`, `@LocalDateTimeSource`, `@LocalTimeSource`
- Random date/time: `@RandomInstantSource`
- Other types: `@StringSource`, enums, booleans
- Values from a function, for data classes and nested objects: `@ValuesFrom` (see [Values From a Function](#values-from-a-function))

## Parameter Combinations

When multiple parameters are generated, the extension uses a **Cartesian product**.

Invocation count:

```text
totalInvocations = product(parameterOptionCounts)
```

Example:

```kotlin
@GeneratedParametersTest
fun combinations(
    @IntRangeSource(min = 1, max = 3) first: Int,      // 3 values
    @IntRangeSource(min = 10, max = 12) second: Int    // 3 values
) {
    // Runs 9 times (3 * 3)
}
```

CI warning: two larger ranges can multiply quickly (for example, `100 x 100 = 10,000` invocations).

A product above one million invocations is refused before any test runs, with a message naming each
parameter and its option count. Raise or lower that with a configuration parameter:

```properties
# junit-platform.properties
com.wesleyhome.test.jupiter.max.permutations=5000000
```

## Pairwise Combinations

`strategy = PAIRWISE` runs enough combinations that **every pair of parameters appears with every
pair of their values**, and no more:

```kotlin
@GeneratedParametersTest(strategy = PAIRWISE)
fun settings(
    @StringSource(["chrome", "firefox", "safari"]) browser: String,
    @StringSource(["windows", "mac", "linux"]) os: String,
    @StringSource(["en", "fr"]) locale: String
) {
    // 9 invocations instead of 18
}
```

Most defects are triggered by one value, or by two interacting. Pairwise keeps that reach for a
fraction of the invocations, and unlike sampling it is a stated guarantee rather than a hope. What
it gives up is combinations needing **three or more** specific values at once: in the example above
`chrome + windows + fr` never runs.

Whether that trade is acceptable depends on what a test asserts, so it is opt in per method and
never a default.

### The saving comes from parameter count, not value count

| Parameters | Cartesian | Pairwise |
| --- | --- | --- |
| 2 params, 3 values each | 9 | **9, no saving at all** |
| 3 params (3, 3, 2) | 18 | 9 |
| 6 params (3, 3, 2, 3, 3, 4) | 648 | ~12-16 |
| 10 params, 3 values each | 59,049 | ~15-20 |

With two parameters the single pair *is* the whole product, so there is nothing to collapse.
Pairwise is a tool for wide test methods; it does nothing for `@IntRangeSource(1..100)` twice, which
still yields 10,000 rows.

### With filters

Say Safari only exists on mac:

```kotlin
@GeneratedParametersTest(strategy = PAIRWISE)
fun rendersOnSupportedPlatforms(
    @StringSource(["chrome", "firefox", "safari"]) browser: String,
    @StringSource(["windows", "mac", "linux"]) os: String,
    @StringSource(["en", "fr"]) locale: String
) { ... }

companion object {
    @JvmStatic
    fun rendersOnSupportedPlatforms_filter(browser: String, os: String): Boolean =
        browser != "safari" || os == "mac"
}
```

The pairs to cover are taken from the combinations the filter accepts, not from the value lists:

- `safari + windows` is not a pair any accepted combination contains, so it is never demanded and
  never reported as missing.
- `safari + mac` **is** reachable, so it is still guaranteed to appear.

Building a covering array first and filtering afterwards would instead drop whichever row happened
to carry `safari + mac`, and the guarantee would quietly stop holding while still being advertised.
That is the reason the two features are wired together rather than layered.

A compiled version of this, checked by the annotation processor, is `examples.Pairwise` in the
`examples` module.

### Notes

- Selection is deterministic. The same test picks the same combinations on every run and every
  machine, and they are emitted in generation order.
- Selection is greedy, so the row count is small but not provably minimal.
- Pairwise reduces what *runs*, not what is generated, so the invocation ceiling still applies to
  the full product and `generated.invocations` still reports it. The report summary names the
  strategy when it is not `CARTESIAN`.

## Filtering Combinations

A Cartesian product generates combinations that are invalid, not merely uninteresting - a `start`
after its `end`, a currency that does not exist in the selected country. Skipping those in the test
body with `Assumptions.assumeTrue` still generates, schedules and runs them, and reports them as
skipped. A filter removes them before an invocation exists.

Name a method after the test and it applies automatically:

```kotlin
@GeneratedParametersTest
fun ordered(
    @IntRangeSource(min = 1, max = 3) start: Int,
    @IntRangeSource(min = 1, max = 3) end: Int
) {
    // runs 6 times, not 9
}

companion object {
    @JvmStatic
    fun ordered_filter(start: Int, end: Int): Boolean = start <= end
}
```

A filter declares only the parameters it is about, matched **by name**, so several small rules
compose instead of one predicate that has to know about everything. A combination runs only if every
filter accepts it. Use a suffix when a test has more than one:

```kotlin
@JvmStatic fun prices_filter_ordered(start: Int, end: Int): Boolean = start <= end
@JvmStatic fun prices_filter_currency(currency: Currency, country: Country): Boolean =
    currency in country.currencies
```

Name filters explicitly to share a rule between tests. Explicit and conventional filters both apply:

```kotlin
@GeneratedParametersTest(filters = ["startBeforeEnd"])
fun prices(...)
```

### Rules

- A filter must be **static**, or declared in a **companion object**. Filtering happens before the
  test instance exists, which is the same constraint `@MethodSource` has. Filters may live in a
  superclass.
- Parameter types must match the generated parameter. A supertype is fine - `Number` accepts a
  generated `Int` - but a filter declaring `Long` against a generated `Int` is rejected rather than
  silently widened by reflection.
- A filter must return `Boolean` and must not return null.
- If every combination is rejected, the test fails and the message names the filters responsible.
- Filtering reduces what *runs*, not what is *generated*, so the invocation ceiling still applies to
  the full product and `generated.invocations` still reports it.

The annotation processor warns about any `*_filter` method that matches no test in its class, so
renaming a test cannot silently leave its rule unused.

## Invocation Display Names

Each invocation is named the way `@ParameterizedTest` names its own, so IDEs and test reports read
generated invocations the same way:

```text
[1] value=1
[2] value=2
```

Override the pattern with `name`:

```kotlin
@GeneratedParametersTest(name = "{index}: {0} squared is {1}")
fun squares(
    @IntRangeSource(min = 1, max = 3) value: Int,
    @IntRangeSource(min = 1, max = 9) square: Int
) {
}
```

| Placeholder | Renders |
| --- | --- |
| `{index}` | the 1-based invocation number |
| `{arguments}` | the generated values, comma separated |
| `{argumentsWithNames}` | the generated values as `name=value`, comma separated |
| `{displayName}` | the test method's own display name |
| `{0}`, `{1}`, ... | a single generated value, by position |

Only generated parameters are listed. A parameter resolved by another extension - `TestInfo`,
`@TempDir`, an injected mock - is not named and does not shift the `{0}`, `{1}` positions.

### Parameter Names

`{argumentsWithNames}` needs the declared parameter names to be present in the compiled class file.
Kotlin records them in its own metadata, so Kotlin test sources need nothing. Java test sources must
be compiled with `-parameters`, or the names fall back to `arg0`, `arg1`:

```kotlin
// Gradle
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}
```

```xml
<!-- Maven -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>
```

## Report Entries

Each test method publishes a summary of what it generated, which is what answers "why does this
test have four thousand cases?" from the report rather than by reading the annotations:

```text
generated.invocations = 4320
generated.parameters  = min=8 x max=6 x increment=5 x dateFormat=6 x ascending=3
```

The generated values themselves are in the display name. They can also be published as structured
entries, one per invocation, for when the display name is truncated by a report viewer or when
something downstream needs to read the values back:

```properties
# junit-platform.properties
com.wesleyhome.test.jupiter.report.values=true
```

```text
generated.left  = 501
generated.right = 1
```

That is off by default because a ten thousand invocation product means ten thousand entries. The
summary is on by default and costs one entry per test method; turn it off with
`com.wesleyhome.test.jupiter.report.summary=false`.

## Null Values

Add `@WithNull` to generate a `null` case alongside a parameter's other values:

```kotlin
@GeneratedParametersTest
fun handlesMissingName(@StringSource(["ada", "grace"]) @WithNull name: String?) {
    // runs with "ada", "grace", null
}
```

```java
@GeneratedParametersTest
void handlesMissingName(@StringSource(values = {"ada", "grace"}) @WithNull String name) {
}
```

The null is generated last, after the parameter's other values.

A Kotlin parameter declared nullable also gets a null case today, inferred from its type. That
inference is Kotlin-only - a Java `Integer` is nullable and never receives one - and it says nothing
at the use site about what the test will run. It still works, but the annotation processor warns
where it is relied on, and it will be removed in a future release. Prefer `@WithNull`.

`@WithNull` on a type that cannot hold null, such as a Kotlin `Int` or a Java `int`, is an error.

## Value Classes

A parameter typed as a Kotlin `@JvmInline value class` takes its values from any source for the
type it wraps. Each value is built through the class's constructor, so its `init` checks run:

```kotlin
@JvmInline
value class Money(val cents: Long) {
    init {
        require(cents >= 0) { "cents must be >= 0" }
    }
}

@GeneratedParametersTest
fun formatsAmounts(@LongSource([0, 1, 250]) amount: Money, @IntSource([1, 2]) quantity: Int) {
    // runs with Money(cents=0), Money(cents=1) and Money(cents=250), each with quantity 1 and 2
}
```

This works for value classes over primitives, `String` and object types. A custom provider can also
produce the value class directly, for a type no built-in source covers:

```kotlin
@JvmInline
value class HouseholdId(val value: UUID)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(HouseholdIdSourceDataProvider::class)
annotation class HouseholdIdSource

class HouseholdIdSourceDataProvider : AbstractAnnotatedParameterDataProvider<HouseholdId, HouseholdIdSource>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<HouseholdId> =
        listOf(HouseholdId(UUID.fromString("00000000-0000-0000-0000-000000000001")))
}

@GeneratedParametersTest
fun loadsHousehold(@HouseholdIdSource id: HouseholdId) {
}
```

If `init` rejects a generated value, the invocation that would have received it fails, and the
others still run. The failure names the parameter, the value and the reason:

```text
Generated value [-1] for parameter [amount] was rejected by Money: IllegalArgumentException: cents must be >= 0
```

A filter can't skip a rejected value either; the invocation runs and fails.

Display names and report entries show the value class's `toString`, such as `Money(cents=250)`.
`@WithNull` works on a nullable value class (`amount: Money?`). On a non-null value class over a
primitive it is an error, because the compiled method takes a `long` there.

Filters can declare the value-class parameter as itself (`fun formatsAmounts_filter(amount: Money)`).
Declaring the underlying type (`amount: Long`) is an error: both compile to `long`, so the Kotlin
declaration is the only thing that tells them apart.

A value class with no source annotation is left to other parameter resolvers, even when it wraps a
type such as `Boolean` that would otherwise be generated.

A compiled version of these, checked by the annotation processor, is `examples.ValueClasses` in the
`examples` module.

## Values From a Function

No annotation can spell out a data class or a nested object. `@ValuesFrom` takes a parameter's
values from a function instead, and combines them with the other parameters like any other source:

```kotlin
data class DateRange(val start: LocalDate, val endInclusive: LocalDate) {
    fun overlaps(other: DateRange): Boolean = start <= other.endInclusive && other.start <= endInclusive
}

class DateRangeTest {

    @GeneratedParametersTest
    fun overlapsIsSymmetric(@ValuesFrom("ranges") a: DateRange, @ValuesFrom("ranges") b: DateRange) {
        // runs with every ordered pair of the three ranges: nine invocations
        assertEquals(a.overlaps(b), b.overlaps(a))
    }

    companion object {
        @JvmStatic
        fun ranges(): List<DateRange> = listOf(
            DateRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
            DateRange(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 15)),
            DateRange(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31))
        )
    }
}
```

The function takes no arguments and returns a `List`, `Sequence` or array of the parameter's type.
It is found by name the way a filter is: static, or in an object or companion object, on the test
class or one of its superclasses. `@JvmStatic` is optional. To share a function between test classes,
name its class as well:

```kotlin
object Fixtures {
    fun ranges(): List<DateRange> = listOf(/* ... */)
}

@GeneratedParametersTest
fun overlapsItself(@ValuesFrom("com.example.Fixtures#ranges") range: DateRange) {
    assertTrue(range.overlaps(range))
}
```

A top-level function is named by the class it compiles into: `com.example.FixturesKt#ranges` for
one declared in `Fixtures.kt`.

```java
@GeneratedParametersTest
void overlapsIsSymmetric(@ValuesFrom("ranges") DateRange a, @ValuesFrom("ranges") DateRange b) {
}

static List<DateRange> ranges() {
    return List.of(/* ... */);
}
```

Values from a function work with everything else:

- **Filters and pairwise:** they see these values as they see any other.
- **`@WithNull`:** adds a null case after the function's values.
- **Value classes:** a function returning `List<Money>` works for a `Money` parameter.
- **Invocation ceiling:** the function's values count toward it. Reading stops once the ceiling is passed, so an endless `Sequence` fails with a message instead of hanging the run.

The element type has to match the parameter's: `List<Long>` is not accepted for a `Money` parameter.
A function whose elements may be null needs a nullable parameter or `@WithNull`. The annotation
processor checks the name, the arguments and the return type at compile time. The same checks run
when the test does, and each failure names the parameter and the function:

```text
@ValuesFrom("ranges") on [a]: [ranges] returns String values, but [a] is DateRange
```

A compiled version of these, checked by the annotation processor, is `examples.ValuesFrom` in the
`examples` module.

## Generated vs Random Values

- Range and explicit-value sources are deterministic by definition.
- `@RandomInstantSource` draws from a fixed seed, so the same bounds produce the same sequence.
- Invocation display names include resolved argument values, so failures show the generated value directly.

Change the seed to explore different values, deliberately rather than by accident:

```kotlin
@GeneratedParametersTest
fun expiry(@RandomInstantSource(min = "2024-01-01T00:00:00Z", max = "2025-01-01T00:00:00Z", size = 10, seed = 99L) at: Instant)
```

### Offset bounds need a fixed clock

A fixed seed only makes a sequence reproducible if the range it draws from is also fixed. With
`useOffset = true` the bounds are resolved against the current instant, so by default they move with
the wall clock and a rerun tomorrow draws from a different window.

Fix the clock to make those reproducible:

```properties
# junit-platform.properties
com.wesleyhome.test.jupiter.clock.fixed.at=2024-01-01T00:00:00Z
com.wesleyhome.test.jupiter.clock.zone=UTC
```

or set one for a class or a run from an extension:

```kotlin
GeneratedParametersClock.set(extensionContext, Clock.fixed(instant, ZoneOffset.UTC))
```

Absolute bounds never depended on the clock and are unaffected by either.

## Performance and Memory

Generation model:

- Parameter value lists are generated eagerly per parameter.
- Test invocations are iterated lazily across the Cartesian product.

Practical guidance:

- Keep ranges intentional.
- Prefer smaller random `size` values.
- Split high-cardinality tests into targeted suites.

## Why Not `@MethodSource`?

| Concern | `@MethodSource` | Generated Sources |
| --- | --- | --- |
| Boilerplate | Requires separate provider methods | Inlined annotation configuration |
| Type safety | Depends on method return wiring | Annotation-driven parameter typing |
| Range/date convenience | Manual list/stream construction | Built-in numeric and date/time sources |
| Compile-time validation | Limited by default | Supported via optional annotation processor |

When a type needs a function, `@ValuesFrom` supplies one parameter's values rather than whole rows
of arguments. The library still combines them, so a function listing three ranges gives every pair
of ranges without writing the pairs out.

### `@CsvSource` vs Generated Parameters

`@CsvSource` for a combinatorial check requires manually listing every pair:

```java
@ParameterizedTest
@CsvSource({
    "1,10",
    "1,20",
    "1,30",
    "2,10",
    "2,20",
    "2,30",
    "3,10",
    "3,20",
    "3,30"
})
void multipliesAllPairs(int left, int right) {
    assertTrue((left * right) > 0);
}
```

Equivalent generated-parameter test (same 3 x 3 Cartesian product, no manual tuple list):

```java
@GeneratedParametersTest
void multipliesAllPairs(
    @IntRangeSource(min = 1, max = 3) int left,
    @IntRangeSource(min = 10, max = 30, increment = 10) int right
) {
    assertTrue((left * right) > 0);
}
```

## Annotation Processor

The optional annotation processor validates supported annotation configurations at compile time.

Example (compile-time error when `min > max`):

```java
@GeneratedParametersTest
void invalid(@IntRangeSource(min = 10, max = 1) int value) {
}
```

It also checks every `@ValuesFrom`: the function exists and can be called before the test instance
exists, takes no arguments, and returns a `List`, `Sequence` or array of the parameter's type.

## Data Flow

```text
[Test Method] -> [Parameter Scan] -> [Per-Parameter Data Generation] -> [Invocation Iteration] -> [Test Execution]
```

## Custom Annotations and Providers

Define a parameter annotation, point it at a provider with `@SourceProvider`, and implement the
provider. `AbstractAnnotatedParameterDataProvider<T, A>` recovers both the parameter type `T` and
the annotation type `A` from the type arguments you supply, so it decides on its own which
parameters to claim:

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(CustomSourceDataProvider::class)
annotation class CustomSource(val min: Int, val max: Int, val increment: Int)

class CustomSourceDataProvider : AbstractAnnotatedParameterDataProvider<Int, CustomSource>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<Int> {
        val annotation = findAnnotation(testParameter)!!
        return (annotation.min..annotation.max step annotation.increment).toList()
    }
}
```

Intermediate classes of your own between the provider and the library base are supported, so shared
behaviour across several providers can live in a base class you control.

For a provider that claims a parameter by type alone, without an annotation, extend
`AbstractParameterDataProvider<T>` instead and override only `createParameterOptionsData`.

To decide for yourself which parameters to claim, implement `ParameterDataProvider<T>` directly and
supply both members:

```kotlin
class CustomSourceDataProvider : ParameterDataProvider<Int> {
    override fun providesDataFor(testParameter: TestParameter): Boolean =
        testParameter.annotations.any { it is CustomSource }

    override fun createParameterOptionsData(testParameter: TestParameter): List<Int> {
        val annotation = testParameter.annotations.filterIsInstance<CustomSource>().first()
        return (annotation.min..annotation.max step annotation.increment).toList()
    }
}
```

## Testing

Run all tests:

```bash
./gradlew test
```
