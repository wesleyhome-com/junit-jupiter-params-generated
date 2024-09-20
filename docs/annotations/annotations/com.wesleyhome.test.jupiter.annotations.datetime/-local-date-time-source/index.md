//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.md)/[LocalDateTimeSource](index.md)

# LocalDateTimeSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [LocalDateTimeSource](index.md)(val values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;, val dateTimeFormat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;yyyy-MM-dd HH:mm&quot;)

Annotation to indicate that the annotated LocalDateTime parameter should be populated with a random value from the provided [values](values.md) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@StringSource(["2023-01-01 00:00", "2022-01-01 00:00", "2021-01-01 00:00"]) value: String) {
// test code
}
// will generate 3 tests with the date values January 1st, 2023, January 1st, 2022, and January 1st, 2021
// the values will be used in the order they are provided

@ParameterizedTest
@ParameterSource
fun test(@LocalDateTimeSource(values = ["01/01/2023 00:00", "02/01/2023 00:00", "03/01/2023 00:00"], dateTimeFormat = "MM/dd/yyyy HH:mm") value: LocalDateTime) {
// test code
}
// will generate 3 tests with the date values January 1st, 2023, February 1st, 2023, and March 1st, 2023
// the values will be used in the order they are provided
```
</code>

## Properties

| Name | Summary |
|---|---|
| [dateTimeFormat](date-time-format.md) | [jvm]<br>val [dateTimeFormat](date-time-format.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [values](values.md) | [jvm]<br>val [values](values.md): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
