//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider](../index.md)/[AbstractAnnotatedParameterDataProvider](index.md)

# AbstractAnnotatedParameterDataProvider

abstract class [AbstractAnnotatedParameterDataProvider](index.md)&lt;[T](index.md) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), [A](index.md) : [Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt; : [AbstractParameterDataProvider](../-abstract-parameter-data-provider/index.md)&lt;[T](index.md)&gt; 

#### Inheritors

| |
|---|
| [StringValueSourceDataProvider](../-string-value-source-data-provider/index.md) |
| [AbstractAnnotatedDateTimeRangeDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-abstract-annotated-date-time-range-data-provider/index.md) |
| [InstantRangeSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-instant-range-source-data-provider/index.md) |
| [InstantValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-instant-value-source-data-provider/index.md) |
| [LocalDateTimeValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-local-date-time-value-source-data-provider/index.md) |
| [LocalDateValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-local-date-value-source-data-provider/index.md) |
| [LocalTimeValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-local-time-value-source-data-provider/index.md) |
| [RandomInstanceSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-random-instance-source-data-provider/index.md) |
| [AbstractAnnotatedNumberRangeDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-abstract-annotated-number-range-data-provider/index.md) |
| [DoubleValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-double-value-source-data-provider/index.md) |
| [FloatValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-float-value-source-data-provider/index.md) |
| [IntValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-int-value-source-data-provider/index.md) |
| [LongValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-long-value-source-data-provider/index.md) |

## Constructors

| | |
|---|---|
| [AbstractAnnotatedParameterDataProvider](-abstract-annotated-parameter-data-provider.md) | [jvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [createParameterOptionsData](../-parameter-data-provider/create-parameter-options-data.md) | [jvm]<br>abstract fun [createParameterOptionsData](../-parameter-data-provider/create-parameter-options-data.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.md)?&gt; |
| [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.md)&gt; |
| [providesDataFor](provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](provides-data-for.md)(testParameter: [TestParameter](../-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
