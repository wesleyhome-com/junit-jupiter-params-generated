---
title: IntRangeSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.number](../index.html)/[IntRangeSource](index.html)



# IntRangeSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [IntRangeSource](index.html)(val min: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val max: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val increment: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

Annotation to indicate that the annotated int parameter should be populated with an integer range from [min](min.html) to [max](max.html) with an [increment](increment.html) step in the [ascending](ascending.html) direction. The default [increment](increment.html) is 1. The default [ascending](ascending.html) is true.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@IntRangeSource(min = 1, max = 300) value: Int) {
// test code
}
// will generate 300 tests with the values 1 to 300
// the values will be in ascending order

@ParameterizedTest
@ParameterSource
fun test(@IntRangeSource(min = 1, max = 300, increment = 1, ascending = false) value: Int) {
// test code
}
// will generate 300 tests with the values 1 to 300
// the values will be in descending order
```
</code>



## Properties


| Name | Summary |
|---|---|
| [ascending](ascending.html) | [jvm]<br>val [ascending](ascending.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [increment](increment.html) | [jvm]<br>val [increment](increment.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1 |
| [max](max.html) | [jvm]<br>val [max](max.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [min](min.html) | [jvm]<br>val [min](min.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |

