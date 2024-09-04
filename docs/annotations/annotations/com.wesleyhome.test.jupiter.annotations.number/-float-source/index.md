---
title: FloatSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.number](../index.html)/[FloatSource](index.html)



# FloatSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [FloatSource](index.html)(val values: [FloatArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float-array/index.html))

Annotation to indicate that the annotated float parameter should be populated with a random value from the provided [values](values.html) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@FloatSource([1.0, 2.0, 3.0]) value: Float) {
// test code
}
// will generate 3 tests with the values 1.0, 2.0, and 3.0
// the values will be used in the order they are provided
```
</code>



## Properties


| Name | Summary |
|---|---|
| [values](values.html) | [jvm]<br>val [values](values.html): [FloatArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float-array/index.html) |

