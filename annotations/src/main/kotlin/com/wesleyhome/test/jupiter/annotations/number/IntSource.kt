package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated int parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@IntSource([1, 2, 3]) value: Int) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1, 2, and 3
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntSource(val values: IntArray)
