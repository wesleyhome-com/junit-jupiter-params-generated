package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to mark that the given LocalTime parameter should be populated with a LocalTime range
 * starting from the minimum value [min] to the maximum value [max] with an [increment] in the direction specified by [ascending].
 * The default [increment] is 1 hour and the default [ascending] order is true.
 *
 * @sample examples.LocalTimeRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalTimeRangeSource(
    /**
     * The minimum value for the LocalTime range. Should be specified in [timeFormat]
     */
    val min: String,
    /**
     * The maximum value for the LocalTime range. Should be specified in [timeFormat]
     */
    val max: String,
    /**
     * A  string representing the period of increment for the LocalTime range. Should follow the ISO-8601 duration format
     * e.g. "PT1h" for 1 hour, "PT30m" for 30 minutes. Default is "PT1h" for 1 hour
     */
    val increment: String = "PT1h",
    /**
     * A boolean to indicate the order of the values in the LocalTime range. Default is true, ascending order.
     */
    val ascending: Boolean = true,
    /**
     * The format to be used for parsing [min] and [max] into LocalTime. Default is "HH:mm"
     */
    val timeFormat: String = "HH:mm"
)
