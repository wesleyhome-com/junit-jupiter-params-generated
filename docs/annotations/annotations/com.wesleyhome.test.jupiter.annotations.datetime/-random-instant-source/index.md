---
title: RandomInstantSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.html)/[RandomInstantSource](index.html)



# RandomInstantSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [RandomInstantSource](index.html)(val size: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html), val minInstant: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val maxInstant: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val minOffset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val maxOffset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val truncateTo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;MINUTES&quot;)

Annotation to be utilized on a parameter of type Instant in a parameterized test. The annotated parameter value will be populated with a randomized value derived from the provided [minInstant](min-instant.html), [maxInstant](max-instant.html), [minOffset](min-offset.html), and [maxOffset](max-offset.html). This will generate [size](size.html) number of random values with in the range specified.



If provided, the [minInstant](min-instant.html) and [maxInstant](max-instant.html) properties will take precedent oven any value provided by [minOffset](min-offset.html) and [maxOffset](max-offset.html)



The [minInstant](min-instant.html) and [maxInstant](max-instant.html) properties should be parsable by [java.time.Instant.parse](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#parse-kotlin.CharSequence-). The [minOffset](min-offset.html) and [maxOffset](max-offset.html) properties should be parsable by [java.time.Period.parse](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html#parse-kotlin.CharSequence-).



The [minOffset](min-offset.html) and [maxOffset](max-offset.html) properties are relative to the current time when the test is run. For example, if the current time is 2023-01-01T00:00:00.000Z, and [minOffset](min-offset.html) is &quot;-P1D&quot; (one day before), the minimum Instant value will be 2022-12-31T00:00:00.000Z. If [maxOffset](max-offset.html) is &quot;P1D&quot; (one day after), the maximum Instant value will be 2023-01-02T00:00:00.000Z.



For instance:

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@RandomInstantSource(size = 10, minInstant = "2023-01-01T00:00:00.000Z", maxInstant = "2023-01-02T01:00:00.000Z") instant: Instant) {
// test code
}
// the above will generate 1 individual test, with 'instant' parameter set to a random Instant value between 2023-01-01T00:00:00.000Z and 2023-01-01T01:00:00.000Z

@ParameterizedTest
@ParameterSource
fun test(@RandomInstantSource(size = 10, startPeriodOffset = "-P1D", endPeriodOffset = "P1D") instant: Instant) {
// test code
}
// the above will generate 1 individual test, with 'instant' parameter set to a random Instant value between 1 day before the current time and 1 day after the current time
```
</code>



## Properties


| Name | Summary |
|---|---|
| [maxInstant](max-instant.html) | [jvm]<br>val [maxInstant](max-instant.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The maximum instant value to be generated. The value is exclusive. This overrides any value by [maxOffset](max-offset.html) |
| [maxOffset](max-offset.html) | [jvm]<br>val [maxOffset](max-offset.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The offset for the maximum Instant value from [java.time.Instant.now](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#now--). For example P1D. This need to be parsable by [java.time.Period](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html). This is overridden by [maxInstant](max-instant.html) if provided. |
| [minInstant](min-instant.html) | [jvm]<br>val [minInstant](min-instant.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The minimum instant value to be generated. The value is inclusive. This overrides any value by [minOffset](min-offset.html) |
| [minOffset](min-offset.html) | [jvm]<br>val [minOffset](min-offset.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The offset for the minimum Instant value from [java.time.Instant.now](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#now--). For example -P1D. This need to be parsable by [java.time.Period](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html). This is overridden by [minInstant](min-instant.html) if provided. |
| [size](size.html) | [jvm]<br>val [size](size.html): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>The number of random values to generate |
| [truncateTo](truncate-to.html) | [jvm]<br>val [truncateTo](truncate-to.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The truncation unit that the starting instant will be truncated to. This is only used if [minOffset](min-offset.html) is provided. This must be a value of [java.time.temporal.ChronoUnit](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/ChronoUnit.html). |

