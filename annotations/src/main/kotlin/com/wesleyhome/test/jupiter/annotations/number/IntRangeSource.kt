package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated int parameter should be populated with an integer range
 * from [min] to [max] with an [increment] step in the [ascending] direction.
 *
 * @sample examples.IntRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntRangeSource(
    /**
     * The minimum value (inclusive)
     */
    val min: Int,
    /**
     * The maximum value (inclusive)
     */
    val max: Int,
    /**
     * The increment between values. Default value is 1.
     */
    val increment: Int = 1,
    /**
     * Whether the values should be in ascending or descending order. Default value is true.
     */
    val ascending: Boolean = true
)
