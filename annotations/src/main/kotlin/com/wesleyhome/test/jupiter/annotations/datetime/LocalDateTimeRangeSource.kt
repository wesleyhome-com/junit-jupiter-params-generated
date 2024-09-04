package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to indicate that the annotated LocalDateTime parameter should be populated with a LocalDateTime range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1 hour.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateTimeRangeSource(min = "2023-01-01 00:00", max = "2023-01-01 23:00") value: LocalDateTime) {
 *     // test code
 *     }
 *     // will generate 24 tests with the values January 1st, 2023 00:00 to January 1st, 2023 23:00
 *     // the values will be in ascending order
 *     // the values will be in increments of 1 hour
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(
 *          @LocalDateTimeRangeSource(
 *              min = "01/01/2023 00:00",
 *              max = "01/01/2023 23:00",
 *              increment = "PT2h",
 *              dateTimeFormat = "MM/dd/yyyy HH:mm",
 *              ascending = false
 *          )
 *          value: LocalDateTime
 *     ) {
 *     // test code
 *     }
 *     // will generate 12 tests with the values January 1st, 2023 00:00 to January 1st, 2023 23:00
 *     // the values will be in descending order
 *     // the values will be in increments of 2 hours
 *
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateTimeRangeSource(
    val min: String,
    val max: String,
    val increment: String = "PT1h",
    val ascending: Boolean = true,
    val dateTimeFormat: String = "yyyy-MM-dd HH:mm"
)
