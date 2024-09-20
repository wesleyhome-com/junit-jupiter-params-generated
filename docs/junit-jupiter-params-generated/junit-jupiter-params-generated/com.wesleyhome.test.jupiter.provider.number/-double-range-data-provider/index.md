//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.number](../index.md)/[DoubleRangeDataProvider](index.md)

# DoubleRangeDataProvider

[jvm]\
class [DoubleRangeDataProvider](index.md) : [AbstractAnnotatedNumberRangeDataProvider](../-abstract-annotated-number-range-data-provider/index.md)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html), [DoubleRangeSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.number/-double-range-source/index.md)&gt;

## Constructors

| | |
|---|---|
| [DoubleRangeDataProvider](-double-range-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [convert](convert.md) | [jvm]<br>open override fun [convert](convert.md)(value: [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)): [Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html) |
| [createParameterOptionsData](../-abstract-annotated-number-range-data-provider/create-parameter-options-data.md) | [jvm]<br>override fun [createParameterOptionsData](../-abstract-annotated-number-range-data-provider/create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[Double](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-double/index.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
