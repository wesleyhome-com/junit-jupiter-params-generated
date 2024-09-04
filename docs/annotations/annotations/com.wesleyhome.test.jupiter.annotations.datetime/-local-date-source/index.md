---
title: LocalDateSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.html)/[LocalDateSource](index.html)



# LocalDateSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [LocalDateSource](index.html)(val values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;, val dateFormat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;yyyy-MM-dd&quot;)

Annotation to indicate that the annotated LocalDate parameter should be populated with a random value from the provided [values](values.html) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@StringSource(["2023-01-01", "2022-01-01", "2021-01-01"]) value: String) {
// test code
}
// will generate 3 tests with the date values January 1st, 2023, January 1st, 2022, and January 1st, 2021
// the values will be used in the order they are provided

@ParameterizedTest
@ParameterSource
fun test(@LocalDateSource(values = ["01/01/2023", "02/01/2023", "03/01/2023"], dateFormat = "MM/dd/yyyy") value: LocalDate) {
// test code
}
// will generate 3 tests with the date values January 1st, 2023, February 1st, 2023, and March 1st, 2023
// the values will be used in the order they are provided
```
</code>



## Properties


| Name | Summary |
|---|---|
| [dateFormat](date-format.html) | [jvm]<br>val [dateFormat](date-format.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [values](values.html) | [jvm]<br>val [values](values.html): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |

