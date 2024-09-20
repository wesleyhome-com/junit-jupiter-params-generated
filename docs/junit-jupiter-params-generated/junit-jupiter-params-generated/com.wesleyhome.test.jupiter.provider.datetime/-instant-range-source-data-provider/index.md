//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.datetime](../index.md)/[InstantRangeSourceDataProvider](index.md)

# InstantRangeSourceDataProvider

[jvm]\
class [InstantRangeSourceDataProvider](index.md) : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.md)&lt;[Instant](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html), [InstantRangeSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-instant-range-source/index.md)&gt;

## Constructors

| | |
|---|---|
| [InstantRangeSourceDataProvider](-instant-range-source-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Instant](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[Instant](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
