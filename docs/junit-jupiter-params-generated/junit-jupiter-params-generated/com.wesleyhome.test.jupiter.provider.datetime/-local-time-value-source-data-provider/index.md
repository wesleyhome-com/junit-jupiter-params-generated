---
title: LocalTimeValueSourceDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider.datetime](../index.html)/[LocalTimeValueSourceDataProvider](index.html)



# LocalTimeValueSourceDataProvider



[jvm]\
class [LocalTimeValueSourceDataProvider](index.html) : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.html)&lt;[LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html), [LocalTimeSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-local-time-source/index.html)&gt;



## Constructors


| | |
|---|---|
| [LocalTimeValueSourceDataProvider](-local-time-value-source-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[LocalTime](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

