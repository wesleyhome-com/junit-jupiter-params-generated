package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated int parameter should be populated with an integer range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@IntRangeSource(min = 1, max = 300) value: Int) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in ascending order
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@IntRangeSource(min = 1, max = 300, increment = 1, ascending = false) value: Int) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in descending order
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntRangeSource(val min: Int, val max: Int, val increment: Int = 1, val ascending: Boolean = true)
