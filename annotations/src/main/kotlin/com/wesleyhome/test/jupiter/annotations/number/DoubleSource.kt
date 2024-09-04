package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated double parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@DoubleSource([1.0, 2.0, 3.0]) value: Double) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1.0, 2.0, and 3.0
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleSource(val values: DoubleArray)
