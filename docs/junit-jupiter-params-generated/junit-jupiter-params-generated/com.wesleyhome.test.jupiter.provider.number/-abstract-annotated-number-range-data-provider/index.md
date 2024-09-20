//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.number](../index.md)/[AbstractAnnotatedNumberRangeDataProvider](index.md)

# AbstractAnnotatedNumberRangeDataProvider

abstract class [AbstractAnnotatedNumberRangeDataProvider](index.md)&lt;[T](index.md) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html), [A](index.md) : [Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt; : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.md)&lt;[T](index.md), [A](index.md)&gt; 

#### Inheritors

| |
|---|
| [DoubleRangeDataProvider](../-double-range-data-provider/index.md) |
| [FloatRangeDataProvider](../-float-range-data-provider/index.md) |
| [IntRangeDataProvider](../-int-range-data-provider/index.md) |
| [LongRangeDataProvider](../-long-range-data-provider/index.md) |

## Constructors

| | |
|---|---|
| [AbstractAnnotatedNumberRangeDataProvider](-abstract-annotated-number-range-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [convert](convert.md) | [jvm]<br>abstract fun [convert](convert.md)(value: [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)): [T](index.md) |
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.md)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.md)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
