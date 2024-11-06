//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.datetime](../index.md)/[LocalDateValueSourceDataProvider](index.md)

# LocalDateValueSourceDataProvider

[jvm]\
class [LocalDateValueSourceDataProvider](index.md) : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.md)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), [LocalDateSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-local-date-source/index.md)&gt;

## Constructors

| | |
|---|---|
| [LocalDateValueSourceDataProvider](-local-date-value-source-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |