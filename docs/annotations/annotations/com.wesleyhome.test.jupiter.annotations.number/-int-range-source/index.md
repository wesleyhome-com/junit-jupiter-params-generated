//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.number](../index.md)/[IntRangeSource](index.md)

# IntRangeSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [IntRangeSource](index.md)(val min: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val max: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val increment: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

Annotation to indicate that the annotated int parameter should be populated with an integer range from [min](min.md) to [max](max.md) with an [increment](increment.md) step in the [ascending](ascending.md) direction. The default [increment](increment.md) is 1. The default [ascending](ascending.md) is true.

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
| [ascending](ascending.md) | [jvm]<br>val [ascending](ascending.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [increment](increment.md) | [jvm]<br>val [increment](increment.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 1 |
| [max](max.md) | [jvm]<br>val [max](max.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [min](min.md) | [jvm]<br>val [min](min.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
