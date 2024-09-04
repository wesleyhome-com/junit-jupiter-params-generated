---
title: FloatRangeSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.number](../index.html)/[FloatRangeSource](index.html)



# FloatRangeSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [FloatRangeSource](index.html)(val min: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), val max: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html), val increment: [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) = 0.5f, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

Annotation to indicate that the annotated float parameter should be populated with a float range from [min](min.html) to [max](max.html) with an [increment](increment.html) step in the [ascending](ascending.html) direction. The default [increment](increment.html) is 0.5f. The default [ascending](ascending.html) is true.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@FloatRangeSource(min = 1.0f, max = 300.0f) value: Float) {
// test code
}
// will generate 600 tests with the values 1.0 to 300.0
// the values will be in ascending order
// the values will be in increments of 0.5

@ParameterizedTest
@ParameterSource
fun test(@FloatRangeSource(min = 1.0f, max = 300.0f, increment = 1.0f, ascending = false) value: Float) {
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
| [increment](increment.html) | [jvm]<br>val [increment](increment.html): [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) = 0.5f |
| [max](max.html) | [jvm]<br>val [max](max.html): [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [min](min.html) | [jvm]<br>val [min](min.html): [Float](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |

