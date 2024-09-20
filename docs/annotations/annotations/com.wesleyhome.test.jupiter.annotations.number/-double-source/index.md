//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.number](../index.md)/[DoubleSource](index.md)

# DoubleSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [DoubleSource](index.md)(val values: [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html))

Annotation to indicate that the annotated double parameter should be populated with a random value from the provided [values](values.md) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@DoubleSource([1.0, 2.0, 3.0]) value: Double) {
// test code
}
// will generate 3 tests with the values 1.0, 2.0, and 3.0
// the values will be used in the order they are provided
```
</code>

## Properties

| Name | Summary |
|---|---|
| [values](values.md) | [jvm]<br>val [values](values.md): [DoubleArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double-array/index.html) |
