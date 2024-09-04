package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to indicate that the annotated LocalDate parameter should be populated with a LocalDate range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1 day.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateRangeSource(min = "2021-01-01", max = "2021-01-31") value: LocalDate) {
 *     // test code
 *     }
 *     // will generate 31 tests with the values January 1st, 2021 to January 31st, 2021
 *     // the values will be in ascending order
 *     // the values will be in increments of 1 day
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(
 *          @LocalDateRangeSource(
 *              min = "01/01/2023",
 *              max = "02/01/2023",
 *              increment = "P2d",
 *              dateFormat = "MM/dd/yyyy",
 *              ascending = false
 *           )
 *           value: LocalDate
 *      ) {
 *     // test code
 *     }
 *     // will generate 16 tests with the values January 1st, 2023 to February 1st, 2023
 *     // the values will be in descending order
 *     // the values will be in increments of 2 days
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateRangeSource(
    val min: String,
    val max: String,
    val increment: String = "P1d",
    val ascending: Boolean = true,
    val dateFormat: String = "yyyy-MM-dd"
)
