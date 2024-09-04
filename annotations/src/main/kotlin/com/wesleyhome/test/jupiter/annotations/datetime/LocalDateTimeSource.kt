package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to indicate that the annotated LocalDateTime parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@StringSource(["2023-01-01 00:00", "2022-01-01 00:00", "2021-01-01 00:00"]) value: String) {
 *     // test code
 *     }
 *     // will generate 3 tests with the date values January 1st, 2023, January 1st, 2022, and January 1st, 2021
 *     // the values will be used in the order they are provided
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateTimeSource(values = ["01/01/2023 00:00", "02/01/2023 00:00", "03/01/2023 00:00"], dateTimeFormat = "MM/dd/yyyy HH:mm") value: LocalDateTime) {
 *     // test code
 *     }
 *     // will generate 3 tests with the date values January 1st, 2023, February 1st, 2023, and March 1st, 2023
 *     // the values will be used in the order they are provided
 * </code>
 **/
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateTimeSource(val values: Array<String>, val dateTimeFormat: String = "yyyy-MM-dd HH:mm")
