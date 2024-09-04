---
title: AbstractAnnotatedNumberRangeDataProvider
---
//[junit-jupiter-params-generated](../../../index.html)/[com.wesleyhome.test.jupiter.provider.number](../index.html)/[AbstractAnnotatedNumberRangeDataProvider](index.html)



# AbstractAnnotatedNumberRangeDataProvider

abstract class [AbstractAnnotatedNumberRangeDataProvider](index.html)&lt;[T](index.html) : [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html), [A](index.html) : [Annotation](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-annotation/index.html)&gt; : [AbstractAnnotatedParameterDataProvider](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/index.html)&lt;[T](index.html), [A](index.html)&gt; 

#### Inheritors


| |
|---|
| [DoubleRangeDataProvider](../-double-range-data-provider/index.html) |
| [FloatRangeDataProvider](../-float-range-data-provider/index.html) |
| [IntRangeDataProvider](../-int-range-data-provider/index.html) |
| [LongRangeDataProvider](../-long-range-data-provider/index.html) |


## Constructors


| | |
|---|---|
| [AbstractAnnotatedNumberRangeDataProvider](-abstract-annotated-number-range-data-provider.html) | [jvm]<br>constructor() |


## Functions


| Name | Summary |
|---|---|
| [convert](convert.html) | [jvm]<br>abstract fun [convert](convert.html)(value: [Number](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-number/index.html)): [T](index.html) |
| [createParameterOptionsData](create-parameter-options-data.html) | [jvm]<br>override fun [createParameterOptionsData](create-parameter-options-data.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](index.html)?&gt; |
| [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html) | [jvm]<br>open override fun [dataProviderFor](../../com.wesleyhome.test.jupiter.provider/-abstract-parameter-data-provider/data-provider-for.html)(): [KClass](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.reflect/-k-class/index.html)&lt;[T](index.html)&gt; |
| [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html) | [jvm]<br>open override fun [providesDataFor](../../com.wesleyhome.test.jupiter.provider/-abstract-annotated-parameter-data-provider/provides-data-for.html)(testParameter: [TestParameter](../../com.wesleyhome.test.jupiter.provider/-test-parameter/index.html)): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

