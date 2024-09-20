//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.number](../index.md)/[LongSource](index.md)

# LongSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [LongSource](index.md)(val values: [LongArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long-array/index.html))

Annotation to indicate that the annotated long parameter should be populated with a random value from the provided [values](values.md) array.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@LongSource([1, 2, 3]) value: Long) {
// test code
}
// will generate 3 tests with the values 1, 2, and 3
// the values will be used in the order they are provided
```
</code>

## Properties

| Name | Summary |
|---|---|
| [values](values.md) | [jvm]<br>val [values](values.md): [LongArray](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long-array/index.html) |
