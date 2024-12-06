# JUnit Jupiter Parameterized Test Extension

This project provides a powerful extension for JUnit Jupiter to enhance parameterized testing capabilities. It offers a flexible and type-safe way to generate test data for various parameter types, including numbers, dates, times, string, and enums.

The JUnit Jupiter Parameterized Test Extension simplifies the process of creating comprehensive test suites by automating the generation of test data. This allows developers to focus on writing test logic while the extension handles the complexities of data provision.

## Repository Structure

The repository is organized into several key directories:

- `annotation-processor`: Contains the annotation processor for validating at compile-time.
- `docs`: Holds the documentation files, including API references and usage guides.
- `examples`: Provides sample code demonstrating how to use the extension.
- `extension`: Contains the core extension code, including data providers and test generators.
- `validation`: Includes validation logic for the generated test data.

Key Files:
- `gradlew` and `gradlew.bat`: Gradle wrapper scripts for building the project.
- `renovate.json`: Configuration file for the Renovate dependency update bot.
- `settings.gradle.kts`: Gradle settings file for the project.

## Usage Instructions

### Installation

To use this extension in your JUnit Jupiter tests, add the following dependency to your project:

```gradle
testImplementation("com.wesleyhome:junit-jupiter-params-generated:1.0.0")
```

Ensure you have JUnit Jupiter 5.7.0 or later in your project dependencies.

### Getting Started

1. Import the necessary annotations and classes:

```kotlin
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
```

2. Create a test method using the `@GeneratedParametersTest` annotation:

```kotlin
@GeneratedParametersTest
fun testWithGeneratedParameters(@IntRangeSource(min = 1, max = 10) value: Int) {
    assertTrue(value in 1..10)
}
```

3. Run your tests as usual with JUnit Jupiter.

### Configuration Options

The extension provides various annotations for different parameter types:

- `@IntRangeSource`, `@LongRangeSource`, `@DoubleRangeSource`, `@FloatRangeSource`: For numeric ranges
- `@InstantRangeSource`, `@LocalDateRangeSource`, `@LocalDateTimeRangeSource`, `@LocalTimeRangeSource`: For date and time ranges
- `@StringSource`: For string values
- `@InstantSource`, `@LocalDateSource`, `@LocalDateTimeSource`, `@LocalTimeSource`: For specific date and time values
- `@RandomInstantSource`: For generating random Instant values
- `@IntSource`, `@LongSource`, `@DoubleSource`, `@FloatSource`: For specific numeric values

Each annotation has specific configuration options. Refer to the documentation for detailed information on each annotation.

### Common Use Cases

1. Testing with a range of integers:

```kotlin
@GeneratedParametersTest
fun testIntRange(@IntRangeSource(min = 1, max = 100, step = 10) value: Int) {
    assertTrue(value in 1..100 step 10)
}
```

```java
@GeneratedParametersTest
void testIntRange(@IntRangeSource(min = 1, max = 100, step = 10) int value) {
    assertTrue(value >= 1 && value <= 100 && (value - 1) % 10 == 0);
}
```

2. Testing with date ranges:

```kotlin
@GeneratedParametersTest
fun testDateRange(@LocalDateRangeSource(min = "2023-01-01", max = "2023-12-31") date: LocalDate) {
    assertTrue(date.year == 2023)
}
```

```java
@GeneratedParametersTest
void testDateRange(@LocalDateRangeSource(min = "2023-01-01", max = "2023-12-31") LocalDate date) {
    assertEquals(2023, date.getYear());
}
```

3. Testing with enum values:

```kotlin
@GeneratedParametersTest
fun testEnumValues(value: TestEnum) {
    assertNotNull(value)
}
```

```java
@GeneratedParametersTest
void testEnumValues(TestEnum value) {
    assertNotNull(value);
}
```

## Annotation Processor

The JUnit Jupiter Parameterized Test Extension includes an annotation processor that can be used for compile-time validation of your test parameters. This helps catch potential issues early in the development process, improving the overall quality and reliability of your tests.

### Setting up the Annotation Processor

To use the annotation processor, add the following dependency to your project:

```gradle
annotationProcessor("com.wesleyhome:junit-jupiter-params-generated-processor:3.0.0")
```

For Maven projects, add the following to your `pom.xml`:

```xml
<dependency>
    <groupId>com.wesleyhome</groupId>
    <artifactId>junit-jupiter-params-generated-processor</artifactId>
    <version>3.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Using the Annotation Processor

The annotation processor automatically validates the usage of parameter annotations in your test classes. Here's an example of how it works with Java:

```java
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import org.junit.jupiter.api.Assertions;

public class AnnotationProcessorExampleTest {

    @GeneratedParametersTest
    void testWithValidRange(@IntRangeSource(min = 1, max = 10) int value) {
        Assertions.assertTrue(value >= 1 && value <= 10);
    }

    @GeneratedParametersTest
    void testWithInvalidRange(@IntRangeSource(min = 10, max = 1) int value) {
        // This will cause a compile-time error
        Assertions.fail("This test should not compile");
    }
}
```

In the example above, the second test method will cause a compile-time error because the `min` value is greater than the `max` value in the `@IntRangeSource` annotation. The annotation processor detects this issue and reports it during compilation, preventing invalid test configurations from making it into your test suite.

### Benefits of Using the Annotation Processor

1. Early detection of configuration errors
2. Improved test reliability
3. Faster feedback loop during development
4. Reduced runtime errors related to parameter generation

By leveraging the annotation processor, you can ensure that your parameterized tests are correctly configured before they are even executed, saving time and improving the overall quality of your test suite.

## Data Flow

The JUnit Jupiter Parameterized Test Extension processes test methods in the following way:

1. The `@GeneratedParametersTest` annotation is detected by JUnit Jupiter.
2. The extension scans the test method parameters for supported annotations.
3. For each annotated parameter, the corresponding data provider is invoked to generate test data.
4. The generated data is used to create test invocations, each with a unique set of parameter values.
5. JUnit Jupiter executes the test method for each set of generated parameter values.

```
[Test Method] -> [Parameter Scan] -> [Data Generation] -> [Test Invocations] -> [Test Execution]
```

This flow allows for efficient and flexible parameterized testing, with the extension handling the complexities of data generation and test invocation.

## Creating Custom Annotations and Data Providers

The JUnit Jupiter Parameterized Test Extension is designed to be extensible, allowing developers to create custom annotations and data providers for specific testing needs. This section explains how to create your own annotations and corresponding data providers.

### Creating a Custom Annotation

To create a custom annotation:

1. Define a new annotation class in your project.
2. Annotate your custom annotation with `@Target(AnnotationTarget.VALUE_PARAMETER)` to specify that it can be used on method parameters.
3. Add any necessary attributes to your annotation.

Example:

```kotlin


### Creating a Custom Data Provider

To create a custom data provider:

1. Create a new class that implements the `ParameterDataProvider<T>` interface, where `T` is the type of data your provider will generate.
2. Implement the `providesDataFor` method to determine if your provider can handle a given `TestParameter`.
3. Implement the `createParameterOptionsData` method to generate the test data based on the annotation attributes.

Example:

```kotlin
class CustomSourceDataProvider : ParameterDataProvider<Int> {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Int> {
        val annotation = testParameter.annotations.filterIsInstance<CustomSource>().first()
        return (annotation.min..annotation.max step annotation.step).toList()
    }
}
```

### Integrating Custom Annotations and Data Providers

To integrate your custom annotation and data provider:

1. Use your custom annotation in your test methods:

```kotlin
@GeneratedParametersTest
fun testWithCustomSource(@CustomSource(min = 1, max = 10, step = 2) value: Int) {
    assertTrue(value in listOf(1, 3, 5, 7, 9))
}
```

By following these steps, you can extend the JUnit Jupiter Parameterized Test Extension with your own custom annotations and data providers, tailoring it to your specific testing requirements.

## Testing & Quality

To run the tests for this project:

```
./gradlew test
```

### Troubleshooting

1. Issue: Tests are not picking up the generated parameters
   - Ensure that you have added the correct dependency to your project.
   - Verify that you are using the `@GeneratedParametersTest` annotation on your test methods.
   - Check that your parameter annotations are correctly configured.

2. Issue: Compilation errors with annotation processor
   - Make sure you have the annotation processor correctly set up in your build configuration.
   - Clean and rebuild your project to ensure the annotation processor runs.

For more detailed troubleshooting, enable debug logging by adding the following to your `logback-test.xml` or `log4j2-test.xml`:

```xml
<logger name="com.wesleyhome.test.jupiter" level="DEBUG"/>
```
