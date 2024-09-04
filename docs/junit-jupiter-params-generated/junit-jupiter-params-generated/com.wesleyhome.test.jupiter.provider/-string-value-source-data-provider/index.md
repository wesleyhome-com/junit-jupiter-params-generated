---
title: StringValueSourceDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider](../index.html)/[StringValueSourceDataProvider](index.html)



# StringValueSourceDataProvider



[jvm]\
class [StringValueSourceDataProvider](index.html) : [AbstractAnnotatedParameterDataProvider](../-abstract-annotated-parameter-data-provider/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), StringSource&gt;



## Constructors


| | |
|---|---|
| [StringValueSourceDataProvider](-string-value-source-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?&gt; |
| [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
| [providesDataFor](../-abstract-annotated-parameter-data-provider/provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](../-abstract-annotated-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

