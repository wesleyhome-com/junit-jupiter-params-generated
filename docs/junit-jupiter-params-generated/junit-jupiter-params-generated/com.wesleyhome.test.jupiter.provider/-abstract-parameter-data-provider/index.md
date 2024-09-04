---
title: AbstractParameterDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider](../index.html)/[AbstractParameterDataProvider](index.html)



# AbstractParameterDataProvider

abstract class [AbstractParameterDataProvider](index.html)&lt;[T](index.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; : [ParameterDataProvider](../-parameter-data-provider/index.html)&lt;[T](index.html)&gt; 

#### Inheritors


| |
|---|
| [AbstractAnnotatedParameterDataProvider](../-abstract-annotated-parameter-data-provider/index.html) |
| [BooleanParameterDataProvider](../-boolean-parameter-data-provider/index.html) |


## Constructors


| | |
|---|---|
| [AbstractParameterDataProvider](-abstract-parameter-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](../-parameter-data-provider/create-parameter-options-data.html) | [jvm]<br>abstract fun [createParameterOptionsData](../-parameter-data-provider/create-parameter-options-data.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.html)?&gt; |
| [dataProviderFor](data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.html)&gt; |
| [providesDataFor](../-parameter-data-provider/provides-data-for.html) | [jvm]<br>open fun [providesDataFor](../-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

