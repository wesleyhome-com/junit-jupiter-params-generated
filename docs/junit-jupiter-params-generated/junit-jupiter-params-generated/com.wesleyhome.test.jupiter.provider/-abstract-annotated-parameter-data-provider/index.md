---
title: AbstractAnnotatedParameterDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider](../index.html)/[AbstractAnnotatedParameterDataProvider](index.html)



# AbstractAnnotatedParameterDataProvider

abstract class [AbstractAnnotatedParameterDataProvider](index.html)&lt;[T](index.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html), [A](index.html) : [Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt; : [AbstractParameterDataProvider](../-abstract-parameter-data-provider/index.html)&lt;[T](index.html)&gt; 

#### Inheritors


| |
|---|
| [StringValueSourceDataProvider](../-string-value-source-data-provider/index.html) |
| [AbstractAnnotatedDateTimeRangeDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-abstract-annotated-date-time-range-data-provider/index.html) |
| [InstantRangeSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-instant-range-source-data-provider/index.html) |
| [InstantValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-instant-value-source-data-provider/index.html) |
| [LocalDateTimeValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-local-date-time-value-source-data-provider/index.html) |
| [LocalDateValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-local-date-value-source-data-provider/index.html) |
| [LocalTimeValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-local-time-value-source-data-provider/index.html) |
| [RandomInstanceSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.datetime/-random-instance-source-data-provider/index.html) |
| [AbstractAnnotatedNumberRangeDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-abstract-annotated-number-range-data-provider/index.html) |
| [DoubleValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-double-value-source-data-provider/index.html) |
| [FloatValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-float-value-source-data-provider/index.html) |
| [IntValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-int-value-source-data-provider/index.html) |
| [LongValueSourceDataProvider](../../com.wesleyhome.test.jupiter.provider.number/-long-value-source-data-provider/index.html) |


## Constructors


| | |
|---|---|
| [AbstractAnnotatedParameterDataProvider](-abstract-annotated-parameter-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [createParameterOptionsData](../-parameter-data-provider/create-parameter-options-data.html) | [jvm]<br>abstract fun [createParameterOptionsData](../-parameter-data-provider/create-parameter-options-data.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.html)?&gt; |
| [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.html)&gt; |
| [providesDataFor](provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](provides-data-for.html)(testParameter: [TestParameter](../-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

