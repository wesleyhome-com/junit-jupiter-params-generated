package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated double parameter should be populated with a double range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 0.5.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@DoubleRangeSource(min = 1.0, max = 300.0) value: Double) {
 *     // test code
 *     }
 *     // will generate 600 tests with the values 1.0 to 300.0
 *     // the values will be in ascending order
 *     // the values will be in increments of 0.5
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@DoubleRangeSource(min = 1.0, max = 300.0, increment = 1.0, ascending = false) value: Double) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1.0 to 300.0
 *     // the values will be in descending order
 *     // the values will be in increments of 1.0
 *  </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleRangeSource(
    val min: Double,
    val max: Double,
    val increment: Double = 0.5,
    val ascending: Boolean = true
)
