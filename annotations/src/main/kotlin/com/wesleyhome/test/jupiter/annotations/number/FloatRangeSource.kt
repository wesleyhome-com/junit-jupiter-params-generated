package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated float parameter should be populated with a float range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 0.5f.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@FloatRangeSource(min = 1.0f, max = 300.0f) value: Float) {
 *     // test code
 *     }
 *     // will generate 600 tests with the values 1.0 to 300.0
 *     // the values will be in ascending order
 *     // the values will be in increments of 0.5
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@FloatRangeSource(min = 1.0f, max = 300.0f, increment = 1.0f, ascending = false) value: Float) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1.0 to 300.0
 *     // the values will be in descending order
 *     // the values will be in increments of 1.0
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class FloatRangeSource(
    val min: Float,
    val max: Float,
    val increment: Float = 0.5f,
    val ascending: Boolean = true
)
