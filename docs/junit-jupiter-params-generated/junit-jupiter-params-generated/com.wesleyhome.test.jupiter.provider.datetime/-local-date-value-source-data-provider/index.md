---
title: LocalDateValueSourceDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider.datetime](../index.html)/[LocalDateValueSourceDataProvider](index.html)



# LocalDateValueSourceDataProvider



[jvm]\
class [LocalDateValueSourceDataProvider](index.html) : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), [LocalDateSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-local-date-source/index.html)&gt;



## Constructors


| | |
|---|---|
| [LocalDateValueSourceDataProvider](-local-date-value-source-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

