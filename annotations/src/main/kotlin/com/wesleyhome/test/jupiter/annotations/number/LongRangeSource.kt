package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated long parameter should be populated with a long range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LongRangeSource(min = 1, max = 300) value: Long) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in ascending order
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LongRangeSource(min = 1, max = 300, increment = 1, ascending = false) value: Long) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in descending order
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LongRangeSource(val min: Long, val max: Long, val increment: Long = 1, val ascending: Boolean = true)
