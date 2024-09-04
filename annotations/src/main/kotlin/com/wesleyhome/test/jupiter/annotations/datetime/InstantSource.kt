package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to be utilized on a parameter of type Instant in a parameterized test. The annotated parameter;s
 * value will be populated with a randomized value derived from the provided [values] array.
 *
 * The [values] array should consist of instant string values, formatted according to the [java.time.format.DateTimeFormatter.ISO_INSTANT]
 * standard.
 *
 * For instance:
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@InstantSource(values = ["2023-01-01T00:00:00.000Z", "2022-01-01T00:00:00.000Z", "2021-01-01T00:00:00.000Z"]) instant: Instant) {
 *     // test code
 *     }
 *     // the above will generate 3 individual tests, with 'instant' parameter set to 2023-01-01T00:00:00.000Z, 2022-01-01T00:00:00.000Z, and 2021-01-01T00:00:00.000Z respectively
 *
 * </code>
 *
 * @property values An array of string representing instant, to be converted into Instant instances.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class InstantSource(val values: Array<String>)
