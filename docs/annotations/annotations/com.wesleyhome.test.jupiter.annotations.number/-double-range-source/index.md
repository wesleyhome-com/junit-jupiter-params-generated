//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.number](../index.md)/[DoubleRangeSource](index.md)

# DoubleRangeSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [DoubleRangeSource](index.md)(val min: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), val max: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), val increment: [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 0.5, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

Annotation to indicate that the annotated double parameter should be populated with a double range from [min](min.md) to [max](max.md) with an [increment](increment.md) step in the [ascending](ascending.md) direction. The default [increment](increment.md) is 0.5. The default [ascending](ascending.md) is true.

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
| [ascending](ascending.md) | [jvm]<br>val [ascending](ascending.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true |
| [increment](increment.md) | [jvm]<br>val [increment](increment.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) = 0.5 |
| [max](max.md) | [jvm]<br>val [max](max.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [min](min.md) | [jvm]<br>val [min](min.md): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
