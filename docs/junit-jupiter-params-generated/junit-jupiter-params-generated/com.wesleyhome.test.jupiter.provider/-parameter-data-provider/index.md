---
title: ParameterDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider](../index.html)/[ParameterDataProvider](index.html)



# ParameterDataProvider

interface [ParameterDataProvider](index.html)&lt;[T](index.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt;

#### Inheritors


| |
|---|
| [AbstractParameterDataProvider](../-abstract-parameter-data-provider/index.html) |
| [EnumParameterDataProvider](../-enum-parameter-data-provider/index.html) |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>abstract fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.html)?&gt; |
| [dataProviderFor](data-provider-for.html) | [jvm]<br>abstract fun [dataProviderFor](data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.html)&gt; |
| [providesDataFor](provides-data-for.html) | [jvm]<br>open fun [providesDataFor](provides-data-for.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

