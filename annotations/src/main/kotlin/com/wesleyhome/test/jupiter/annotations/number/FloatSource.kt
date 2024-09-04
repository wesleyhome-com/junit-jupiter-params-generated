package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated float parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@FloatSource([1.0, 2.0, 3.0]) value: Float) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1.0, 2.0, and 3.0
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class FloatSource(val values: FloatArray)
