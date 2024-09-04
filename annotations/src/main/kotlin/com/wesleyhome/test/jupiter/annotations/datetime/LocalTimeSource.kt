package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to be utilized on a parameter of type LocalTime in a parametrized test. The annotated parameter's
 * value will be populated with a randomized value derived from the provided [values] array.
 *
 * The [values] array should consist of time string values, formatted according to the [timeFormat] property.
 *
 * Default value for [timeFormat] is "HH:mm:ss".
 *
 * For instance:
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalTimeSource(values = ["12:00", "14:30", "16:45"]) time: LocalTime) {
 *     // test code
 *     }
 *     // the above will generate 3 individual tests, with 'time' parameter set to 12:00:00, 14:30:00, and 16:45:00 respectively
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalTimeSource(values = ["12:00:00", "14:30:00", "16:45:01"], timeFormat = "HH:mm:ss) time: LocalTime) {
 *     // test code
 *     }
 *     // the above will generate 3 individual tests, with 'time' parameter set to 12:00:00, 14:30:00, and 16:45:01 respectively
 *
 * </code>
 *
 * @property values An array of string representing time, to be converted into LocalTime instances.
 * @property timeFormat A String representing the pattern to be used for parsing the [values] into LocalTime instances.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalTimeSource(val values: Array<String>, val timeFormat: String = "HH:mm")
