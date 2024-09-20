//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.datetime](../index.md)/[AbstractAnnotatedDateTimeRangeDataProvider](index.md)

# AbstractAnnotatedDateTimeRangeDataProvider

abstract class [AbstractAnnotatedDateTimeRangeDataProvider](index.md)&lt;[T](index.md) : [Comparable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-comparable/index.html)&lt;[T](index.md)&gt;, [A](index.md) : [Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt; : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.md)&lt;[T](index.md), [A](index.md)&gt; 

#### Inheritors

| |
|---|
| [LocalDateRangeDataProvider](../-local-date-range-data-provider/index.md) |
| [LocalDateTimeRangeDataProvider](../-local-date-time-range-data-provider/index.md) |
| [LocalTimeRangeDataProvider](../-local-time-range-data-provider/index.md) |

## Constructors

| | |
|---|---|
| [AbstractAnnotatedDateTimeRangeDataProvider](-abstract-annotated-date-time-range-data-provider.md) | [jvm]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [formatPropertyName](format-property-name.md) | [jvm]<br>abstract val [formatPropertyName](format-property-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [convert](convert.md) | [jvm]<br>abstract fun [convert](convert.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), format: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](index.md) |
| [createParameterOptionsData](create-parameter-options-data.md) | [jvm]<br>override fun [createParameterOptionsData](create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.md)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.md)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toList](to-list.md) | [jvm]<br>abstract fun [toList](to-list.md)(min: [T](index.md), max: [T](index.md), increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.md)&gt; |
