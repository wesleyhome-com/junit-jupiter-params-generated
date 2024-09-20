//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider](../index.md)/[StringValueSourceDataProvider](index.md)

# StringValueSourceDataProvider

[jvm]\
class [StringValueSourceDataProvider](index.md) : [AbstractAnnotatedParameterDataProvider](../-abstract-annotated-parameter-data-provider/index.md)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), [StringSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations/-string-source/index.md)&gt;

## Constructors

| | |
|---|---|
| [StringValueSourceDataProvider](-string-value-source-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>open override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?&gt; |
| [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)&gt; |
| [providesDataFor](../-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
