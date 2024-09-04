---
title: IntSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.number](../index.html)/[IntSource](index.html)



# IntSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [IntSource](index.html)(val values: [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html))

Annotation to indicate that the annotated int parameter should be populated with a random value from the provided [values](values.html) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@IntSource([1, 2, 3]) value: Int) {
// test code
}
// will generate 3 tests with the values 1, 2, and 3
// the values will be used in the order they are provided
```
</code>



## Properties


| Name | Summary |
|---|---|
| [values](values.html) | [jvm]<br>val [values](values.html): [IntArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html) |

