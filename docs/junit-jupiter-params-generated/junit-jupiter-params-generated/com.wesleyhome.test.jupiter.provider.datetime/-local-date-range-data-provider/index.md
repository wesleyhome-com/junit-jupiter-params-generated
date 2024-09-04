---
title: LocalDateRangeDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider.datetime](../index.html)/[LocalDateRangeDataProvider](index.html)



# LocalDateRangeDataProvider



[jvm]\
class [LocalDateRangeDataProvider](index.html) : [AbstractAnnotatedDateTimeRangeDataProvider](../-abstract-annotated-date-time-range-data-provider/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), [LocalDateRangeSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-local-date-range-source/index.html)&gt;



## Constructors


| | |
|---|---|
| [LocalDateRangeDataProvider](-local-date-range-data-provider.html) | [jvm]<br>constructor() |


## Properties


| Name | Summary |
|---|---|
| [formatPropertyName](format-property-name.html) | [jvm]<br>open override val [formatPropertyName](format-property-name.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |


## Functions


| Name | Summary |
|---|---|
| [convert](convert.html) | [jvm]<br>open override fun [convert](convert.html)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), format: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html) |
| [createParameterOptionsData](../-abstract-annotated-date-time-range-data-provider/create-parameter-options-data.html) | [jvm]<br>override fun [createParameterOptionsData](../-abstract-annotated-date-time-range-data-provider/create-parameter-options-data.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toList](to-list.html) | [jvm]<br>open override fun [toList](to-list.html)(min: [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), max: [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)&gt; |

