---
title: DoubleRangeSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.number](../index.html)/[DoubleRangeSource](index.html)



# DoubleRangeSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [DoubleRangeSource](index.html)(val min: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), val max: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), val increment: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 0.5, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

Annotation to indicate that the annotated double parameter should be populated with a double range from [min](min.html) to [max](max.html) with an [increment](increment.html) step in the [ascending](ascending.html) direction. The default [increment](increment.html) is 0.5. The default [ascending](ascending.html) is true.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@DoubleRangeSource(min = 1.0, max = 300.0) value: Double) {
// test code
}
// will generate 600 tests with the values 1.0 to 300.0
// the values will be in ascending order
// the values will be in increments of 0.5

@ParameterizedTest
@ParameterSource
fun test(@DoubleRangeSource(min = 1.0, max = 300.0, increment = 1.0, ascending = false) value: Double) {
// test code
}
// will generate 300 tests with the values 1.0 to 300.0
// the values will be in descending order
// the values will be in increments of 1.0
```
</code>



## Properties


| Name | Summary |
|---|---|
| [ascending](ascending.html) | [jvm]<br>val [ascending](ascending.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [increment](increment.html) | [jvm]<br>val [increment](increment.html): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 0.5 |
| [max](max.html) | [jvm]<br>val [max](max.html): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [min](min.html) | [jvm]<br>val [min](min.html): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |

