//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.md)/[LocalDateRangeSource](index.md)

# LocalDateRangeSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [LocalDateRangeSource](index.md)(val min: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val max: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;P1d&quot;, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, val dateFormat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;yyyy-MM-dd&quot;)

Annotation to indicate that the annotated LocalDate parameter should be populated with a LocalDate range from [min](min.md) to [max](max.md) with an [increment](increment.md) step in the [ascending](ascending.md) direction. The default [increment](increment.md) is 1 day. The default [ascending](ascending.md) is true.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@LocalDateRangeSource(min = "2021-01-01", max = "2021-01-31") value: LocalDate) {
// test code
}
// will generate 31 tests with the values January 1st, 2021 to January 31st, 2021
// the values will be in ascending order
// the values will be in increments of 1 day

@ParameterizedTest
@ParameterSource
fun test(
     @LocalDateRangeSource(
         min = "01/01/2023",
         max = "02/01/2023",
         increment = "P2d",
         dateFormat = "MM/dd/yyyy",
         ascending = false
      )
      value: LocalDate
 ) {
// test code
}
// will generate 16 tests with the values January 1st, 2023 to February 1st, 2023
// the values will be in descending order
// the values will be in increments of 2 days
```
</code>

## Properties

| Name | Summary |
|---|---|
| [ascending](ascending.md) | [jvm]<br>val [ascending](ascending.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [dateFormat](date-format.md) | [jvm]<br>val [dateFormat](date-format.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [increment](increment.md) | [jvm]<br>val [increment](increment.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [max](max.md) | [jvm]<br>val [max](max.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [min](min.md) | [jvm]<br>val [min](min.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
