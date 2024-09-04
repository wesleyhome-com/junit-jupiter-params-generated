---
title: LocalDateTimeRangeSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.html)/[LocalDateTimeRangeSource](index.html)



# LocalDateTimeRangeSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [LocalDateTimeRangeSource](index.html)(val min: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val max: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;PT1h&quot;, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, val dateTimeFormat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;yyyy-MM-dd HH:mm&quot;)

Annotation to indicate that the annotated LocalDateTime parameter should be populated with a LocalDateTime range from [min](min.html) to [max](max.html) with an [increment](increment.html) step in the [ascending](ascending.html) direction. The default [increment](increment.html) is 1 hour. The default [ascending](ascending.html) is true.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@LocalDateTimeRangeSource(min = "2023-01-01 00:00", max = "2023-01-01 23:00") value: LocalDateTime) {
// test code
}
// will generate 24 tests with the values January 1st, 2023 00:00 to January 1st, 2023 23:00
// the values will be in ascending order
// the values will be in increments of 1 hour

@ParameterizedTest
@ParameterSource
fun test(
     @LocalDateTimeRangeSource(
         min = "01/01/2023 00:00",
         max = "01/01/2023 23:00",
         increment = "PT2h",
         dateTimeFormat = "MM/dd/yyyy HH:mm",
         ascending = false
     )
     value: LocalDateTime
) {
// test code
}
// will generate 12 tests with the values January 1st, 2023 00:00 to January 1st, 2023 23:00
// the values will be in descending order
// the values will be in increments of 2 hours
```
</code>



## Properties


| Name | Summary |
|---|---|
| [ascending](ascending.html) | [jvm]<br>val [ascending](ascending.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [dateTimeFormat](date-time-format.html) | [jvm]<br>val [dateTimeFormat](date-time-format.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [increment](increment.html) | [jvm]<br>val [increment](increment.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [max](max.html) | [jvm]<br>val [max](max.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [min](min.html) | [jvm]<br>val [min](min.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

