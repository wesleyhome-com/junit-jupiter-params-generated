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

| Component | Supported |
| --- | --- |
| Java | 17+ |
| Kotlin/JVM | 2.3.x |
| JUnit Jupiter | 6.0.3+ |
| Gradle | Supported (`testImplementation`, optional `ksp`) |
| Maven | Supported (`test` scope for extension) |

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

## Generated vs Random Values

- Range and explicit-value sources are deterministic by definition.
- `@RandomInstantSource` is deterministic by default in this library (fixed internal seed).
- Invocation display names include resolved argument values, so failures show the generated value directly.

Because random generation is deterministic by default, rerunning the same test configuration reproduces the same sequence.

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

### `@CsvSource` vs Generated Parameters

`@CsvSource` for a range-style check:

```java
@ParameterizedTest
@CsvSource({
    "1,true",
    "2,true",
    "3,true",
    "4,true",
    "5,true"
})
void valuesAreInRange(int value, boolean expected) {
    assertEquals(expected, value >= 1 && value <= 5);
}
```

Equivalent generated-parameter test:

```java
@GeneratedParametersTest
void valuesAreInRange(@IntRangeSource(min = 1, max = 5) int value) {
    assertTrue(value >= 1 && value <= 5);
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

## Data Flow

```text
[Test Method] -> [Parameter Scan] -> [Per-Parameter Data Generation] -> [Invocation Iteration] -> [Test Execution]
```

## Custom Annotations and Providers

You can create custom providers by:

1. Defining a parameter annotation.
2. Mapping it with `@SourceProvider(...)`.
3. Implementing `ParameterDataProvider<T>`.

Example:

```kotlin
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(CustomSourceDataProvider::class)
annotation class CustomSource(val min: Int, val max: Int, val increment: Int)

class CustomSourceDataProvider : ParameterDataProvider<Int> {
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
