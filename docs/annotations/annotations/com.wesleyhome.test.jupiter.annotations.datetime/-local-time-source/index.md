//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.md)/[LocalTimeSource](index.md)

# LocalTimeSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [LocalTimeSource](index.md)(val values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;, val timeFormat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;HH:mm&quot;)

Annotation to be utilized on a parameter of type LocalTime in a parametrized test. The annotated parameter's value will be populated with a randomized value derived from the provided [values](values.md) array.

The [values](values.md) array should consist of time string values, formatted according to the [timeFormat](time-format.md) property.

Default value for [timeFormat](time-format.md) is &quot;HH:mm:ss&quot;.

For instance:

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@LocalTimeSource(values = ["12:00", "14:30", "16:45"]) time: LocalTime) {
// test code
}
// the above will generate 3 individual tests, with 'time' parameter set to 12:00:00, 14:30:00, and 16:45:00 respectively

@ParameterizedTest
@ParameterSource
fun test(@LocalTimeSource(values = ["12:00:00", "14:30:00", "16:45:01"], timeFormat = "HH:mm:ss) time: LocalTime) {
// test code
}
// the above will generate 3 individual tests, with 'time' parameter set to 12:00:00, 14:30:00, and 16:45:01 respectively
```
</code>

## Properties

| Name | Summary |
|---|---|
| [timeFormat](time-format.md) | [jvm]<br>val [timeFormat](time-format.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>A String representing the pattern to be used for parsing the [values](values.md) into LocalTime instances. |
| [values](values.md) | [jvm]<br>val [values](values.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;<br>An array of string representing time, to be converted into LocalTime instances. |
