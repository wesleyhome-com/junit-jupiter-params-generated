//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.md)/[InstantSource](index.md)

# InstantSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [InstantSource](index.md)(val values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;)

Annotation to be utilized on a parameter of type Instant in a parameterized test. The annotated parameter;s value will be populated with a randomized value derived from the provided [values](values.md) array.

The [values](values.md) array should consist of instant string values, formatted according to the [java.time.format.DateTimeFormatter.ISO_INSTANT](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_INSTANT--) standard.

For instance:

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@InstantSource(values = ["2023-01-01T00:00:00.000Z", "2022-01-01T00:00:00.000Z", "2021-01-01T00:00:00.000Z"]) instant: Instant) {
// test code
}
// the above will generate 3 individual tests, with 'instant' parameter set to 2023-01-01T00:00:00.000Z, 2022-01-01T00:00:00.000Z, and 2021-01-01T00:00:00.000Z respectively
```
</code>

## Properties

| Name | Summary |
|---|---|
| [values](values.md) | [jvm]<br>val [values](values.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;<br>An array of string representing instant, to be converted into Instant instances. |
