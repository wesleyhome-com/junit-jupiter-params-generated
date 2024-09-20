//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider](../index.md)/[ParameterDataProvider](index.md)

# ParameterDataProvider

interface [ParameterDataProvider](index.md)&lt;[T](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt;

#### Inheritors

| |
|---|
| [AbstractParameterDataProvider](../-abstract-parameter-data-provider/index.md) |
| [EnumParameterDataProvider](../-enum-parameter-data-provider/index.md) |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>abstract fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.md)?&gt; |
| [dataProviderFor](data-provider-for.md) | [jvm]<br>abstract fun [dataProviderFor](data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.md)&gt; |
| [providesDataFor](provides-data-for.md) | [jvm]<br>open fun [providesDataFor](provides-data-for.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
