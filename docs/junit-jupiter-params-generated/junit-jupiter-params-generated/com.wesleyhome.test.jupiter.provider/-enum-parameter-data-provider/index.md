---
title: EnumParameterDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider](../index.html)/[EnumParameterDataProvider](index.html)



# EnumParameterDataProvider



[jvm]\
@[DefaultProvider](../../com.wesleyhome.test.jupiter.annotations/-default-provider/index.html)



class [EnumParameterDataProvider](index.html) : [ParameterDataProvider](../-parameter-data-provider/index.html)&lt;[Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;*&gt;&gt;



## Constructors


| | |
|---|---|
| [EnumParameterDataProvider](-enum-parameter-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;*&gt;?&gt; |
| [dataProviderFor](data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;*&gt;&gt; |
| [providesDataFor](provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](provides-data-for.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

