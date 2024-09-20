//[junit-jupiter-params-generated](../../../index.md)/[com.wesleyhome.test.jupiter.provider.datetime](../index.md)/[LocalDateRangeDataProvider](index.md)

# LocalDateRangeDataProvider

[jvm]\
class [LocalDateRangeDataProvider](index.md) : [AbstractAnnotatedDateTimeRangeDataProvider](../-abstract-annotated-date-time-range-data-provider/index.md)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), [LocalDateRangeSource](../../../../annotations/annotations/com.wesleyhome.test.jupiter.annotations.datetime/-local-date-range-source/index.md)&gt;

## Constructors

| | |
|---|---|
| [LocalDateRangeDataProvider](-local-date-range-data-provider.md) | [jvm]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [formatPropertyName](format-property-name.md) | [jvm]<br>open override val [formatPropertyName](format-property-name.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [convert](convert.md) | [jvm]<br>open override fun [convert](convert.md)(value: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), format: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html) |
| [createParameterOptionsData](../-abstract-annotated-date-time-range-data-provider/create-parameter-options-data.md) | [jvm]<br>override fun [createParameterOptionsData](../-abstract-annotated-date-time-range-data-provider/create-parameter-options-data.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.md)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.md)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.md)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [toList](to-list.md) | [jvm]<br>open override fun [toList](to-list.md)(min: [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), max: [LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html), increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[LocalDate](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)&gt; |
