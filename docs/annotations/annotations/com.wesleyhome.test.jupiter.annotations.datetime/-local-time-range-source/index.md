//[annotations](../../../index.md)/[com.wesleyhome.test.jupiter.annotations.datetime](../index.md)/[LocalTimeRangeSource](index.md)

# LocalTimeRangeSource

[jvm]\
@[Target](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-target/index.html)(allowedTargets = [[AnnotationTarget.VALUE_PARAMETER](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.annotation/-annotation-target/-v-a-l-u-e_-p-a-r-a-m-e-t-e-r/index.html)])

annotation class [LocalTimeRangeSource](index.md)(val min: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val max: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), val increment: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;PT1h&quot;, val ascending: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true, val timeFormat: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) = &quot;HH:mm&quot;)

Annotation to mark that the given LocalTime parameter should be populated with a LocalTime range starting from the minimum value [min](min.md) to the maximum value [max](max.md) with an [increment](increment.md) in the direction specified by [ascending](ascending.md). The default [increment](increment.md) is 1 hour and the default [ascending](ascending.md) order is true.

Example of usage: <code>     @ParameterizedTest     @ParameterSource     fun test(@LocalTimeRangeSource(min = &quot;12:00&quot;, max = &quot;14:00&quot;, increment = &quot;PT1h&quot;, ascending = true, dateTimeFormat = &quot;HH:mm&quot;) time: LocalTime) {         // test code     }     // This will generate 3 tests with the time values 12:00:00, 13:00:00, and 14:00:00     // The values will be in 1 hour increments in ascending order </code>

## Properties

| Name | Summary |
|---|---|
| [ascending](ascending.md) | [jvm]<br>val [ascending](ascending.md): [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) = true<br>A boolean to indicate the order of the values in the LocalTime range. Default is true, ascending order. |
| [increment](increment.md) | [jvm]<br>val [increment](increment.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>A string representing the period of increment for the LocalTime range. Should follow the ISO-8601 duration format     e.g. &quot;PT1h&quot; for 1 hour, &quot;PT30m&quot; for 30 minutes. Default is &quot;PT1h&quot; for 1 hour. |
| [max](max.md) | [jvm]<br>val [max](max.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>A string representing the maximum value for the LocalTime range. Should be specified in [timeFormat](time-format.md). |
| [min](min.md) | [jvm]<br>val [min](min.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>A string representing the minimum value for the LocalTime range. Should be specified in [timeFormat](time-format.md). |
| [timeFormat](time-format.md) | [jvm]<br>val [timeFormat](time-format.md): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>A string representing the format to be used for parsing [min](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html) and [max](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/index.html) into LocalTime. Default is &quot;HH:mm&quot;. |
