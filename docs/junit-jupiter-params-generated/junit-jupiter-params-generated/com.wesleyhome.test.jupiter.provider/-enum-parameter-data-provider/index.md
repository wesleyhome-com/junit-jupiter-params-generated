//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider](../index.md)/[EnumParameterDataProvider](index.md)

# EnumParameterDataProvider

[jvm]\
@[DefaultProvider](../../com.wesleyhome.test.jupiter.annotations/-default-provider/index.md)

class [EnumParameterDataProvider](index.md) : [ParameterDataProvider](../-parameter-data-provider/index.md)&lt;[Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;*&gt;&gt;

## Constructors

| | |
|---|---|
| [EnumParameterDataProvider](-enum-parameter-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;*&gt;?&gt; |
| [dataProviderFor](data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;*&gt;&gt; |
| [providesDataFor](provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](provides-data-for.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
