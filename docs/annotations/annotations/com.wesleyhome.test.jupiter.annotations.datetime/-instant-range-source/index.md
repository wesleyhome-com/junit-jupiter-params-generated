---
title: InstantRangeSource
---
//[annotations](../../../index.html)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.html)/[InstantRangeSource](index.html)



# InstantRangeSource



[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])



annotation class [InstantRangeSource](index.html)(val minInstant: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val maxInstant: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val minOffset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val maxOffset: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;&quot;, val truncateTo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;MINUTES&quot;, val increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;PT1h&quot;, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true)

Annotation to indicate that the annotated Instant parameter should be populated with an Instant range from [minInstant](min-instant.html) up to [maxInstant](max-instant.html) or [minOffset](min-offset.html) up to [maxOffset](max-offset.html) with an [increment](increment.html). The default [increment](increment.html) is 1 hour. If [ascending](ascending.html) is false, the process will be reversed. The default [ascending](ascending.html) is true.

<code>```kotlin
@ParameterizedTest
@ParameterSource
fun test(@InstantRangeSource(minInstant = "2023-01-01T00:00:00Z", maxInstant = "2023-01-01T23:00:00Z") value: Instant) {
// test code
}
// will generate 24 tests with the values January 1st, 2023 00:00:00Z to January 1st, 2023 23:00:00Z
// the values will be in ascending order
// the values will be in increments of 1 hour

@ParameterizedTest
@ParameterSource
fun test(
     @InstantRangeSource(
         minInstant = "01/01/2023T00:00:00Z",
         maxInstant = "01/01/2023T23:00:00Z",
         increment = "PT2h",
         ascending = false
     )
     value: Instant
) {
// test code
}
// will generate 12 tests with the values January 1st, 2023 00:00:00Z to January 1st, 2023 23:00:00Z
// the values will be in descending order
// the values will be in increments of 2 hours
```
</code>



## Properties


| Name | Summary |
|---|---|
| [ascending](ascending.html) | [jvm]<br>val [ascending](ascending.html): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true<br>The direction the values will be generated. If true, the range will start with [minInstant](min-instant.html) and increment until it reaches [maxInstant](max-instant.html). If false, the range will start with [maxInstant](max-instant.html) and decrement until it reaches [minInstant](min-instant.html). |
| [increment](increment.html) | [jvm]<br>val [increment](increment.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>How much time to increment the value for each test. If [ascending](ascending.html) is false, this value will be negated. The default value is 1 hour. Should follow the ISO-8601 duration format e.g. &quot;PT1h&quot; for 1 hour, &quot;PT30m&quot; for 30 minutes. |
| [maxInstant](max-instant.html) | [jvm]<br>val [maxInstant](max-instant.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The maximum instant value that the range of values will include. Depending on [increment](increment.html) and [ascending](ascending.html), this value may or may not be used as a parameter value. |
| [maxOffset](max-offset.html) | [jvm]<br>val [maxOffset](max-offset.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The offset for the maximum Instant value from [java.time.Instant.now](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#now--). For example P1D. This need to be parsable by [java.time.Period](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html). This is overridden by [maxInstant](max-instant.html) if provided. |
| [minInstant](min-instant.html) | [jvm]<br>val [minInstant](min-instant.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The minimum instant value that the range of values will include. Depending on [increment](increment.html) and [ascending](ascending.html), this value may or may not be used as a parameter value. |
| [minOffset](min-offset.html) | [jvm]<br>val [minOffset](min-offset.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The offset for the minimum Instant value from [java.time.Instant.now](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html#now--). For example -P1D. This need to be parsable by [java.time.Period](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html). This is overridden by [minInstant](min-instant.html) if provided. |
| [truncateTo](truncate-to.html) | [jvm]<br>val [truncateTo](truncate-to.html): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The truncation unit that the starting instant will be truncated to. This is only used if [minOffset](min-offset.html) is provided. This must be a value of [java.time.temporal.ChronoUnit](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/ChronoUnit.html). |

