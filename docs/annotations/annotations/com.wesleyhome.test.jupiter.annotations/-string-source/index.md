---
title: StringSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations](../index.html)/[StringSource](index.html)



# StringSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [StringSource](index.html)(val values: [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt;)

Annotation to indicate that the annotated String parameter should be populated with a random value from the provided [values](values.html) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@StringSource(["a", "b", "c"]) value: String) {
// test code
}
// will generate 3 tests with the values "a", "b", and "c"
// the values will be used in the order they are provided
```
</code>



## Properties


| Name | Summary |
|---|---|
| [values](values.html) | [jvm]<br>val [values](values.html): [Array](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |

