package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to mark that the given LocalTime parameter should be populated with a LocalTime range
 * starting from the minimum value [min] to the maximum value [max] with an [increment] in the direction specified by [ascending].
 * The default [increment] is 1 hour and the default [ascending] order is true.
 *
 * Example of usage:
 * <code>
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalTimeRangeSource(min = "12:00", max = "14:00", increment = "PT1h", ascending = true, dateTimeFormat = "HH:mm") time: LocalTime) {
 *         // test code
 *     }
 *     // This will generate 3 tests with the time values 12:00:00, 13:00:00, and 14:00:00
 *     // The values will be in 1 hour increments in ascending order
 * </code>
 *
 * @property min A string representing the minimum value for the LocalTime range. Should be specified in [timeFormat].
 * @property max A string representing the maximum value for the LocalTime range. Should be specified in [timeFormat].
 * @property increment A string representing the period of increment for the LocalTime range. Should follow the ISO-8601 duration format
 *                     e.g. "PT1h" for 1 hour, "PT30m" for 30 minutes. Default is "PT1h" for 1 hour.
 * @property ascending A boolean to indicate the order of the values in the LocalTime range. Default is true, ascending order.
 * @property timeFormat A string representing the format to be used for parsing [min] and [max] into LocalTime. Default is "HH:mm".
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalTimeRangeSource(
    val min: String,
    val max: String,
    val increment: String = "PT1h",
    val ascending: Boolean = true,
    val timeFormat: String = "HH:mm"
)
