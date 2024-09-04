---
title: AbstractAnnotatedDateTimeRangeDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider.datetime](../index.html)/[AbstractAnnotatedDateTimeRangeDataProvider](index.html)



# AbstractAnnotatedDateTimeRangeDataProvider

abstract class [AbstractAnnotatedDateTimeRangeDataProvider](index.html)&lt;[T](index.html) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[T](index.html)&gt;, [A](index.html) : [Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt; : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.html)&lt;[T](index.html), [A](index.html)&gt; 

#### Inheritors


| |
|---|
| [LocalDateRangeDataProvider](../-local-date-range-data-provider/index.html) |
| [LocalDateTimeRangeDataProvider](../-local-date-time-range-data-provider/index.html) |
| [LocalTimeRangeDataProvider](../-local-time-range-data-provider/index.html) |


## Constructors


| | |
|---|---|
| [AbstractAnnotatedDateTimeRangeDataProvider](-abstract-annotated-date-time-range-data-provider.html) | [jvm]<br>constructor() |


## Properties


| Name | Summary |
|---|---|
| [formatPropertyName](format-property-name.html) | [jvm]<br>abstract val [formatPropertyName](format-property-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |


## Functions


| Name | Summary |
|---|---|
| [convert](convert.html) | [jvm]<br>abstract fun [convert](convert.html)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), format: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](index.html) |
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>override fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toList](to-list.html) | [jvm]<br>abstract fun [toList](to-list.html)(min: [T](index.html), max: [T](index.html), increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.html)&gt; |

