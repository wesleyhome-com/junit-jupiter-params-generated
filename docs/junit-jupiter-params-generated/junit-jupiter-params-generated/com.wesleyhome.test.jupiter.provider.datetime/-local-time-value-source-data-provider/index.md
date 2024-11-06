//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.datetime](../index.md)/[LocalTimeValueSourceDataProvider](index.md)

# LocalTimeValueSourceDataProvider

[jvm]\
class [LocalTimeValueSourceDataProvider](index.md) : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.md)&lt;[LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html), [LocalTimeSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-local-time-source/index.md)&gt;

## Constructors

| | |
|---|---|
| [LocalTimeValueSourceDataProvider](-local-time-value-source-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |