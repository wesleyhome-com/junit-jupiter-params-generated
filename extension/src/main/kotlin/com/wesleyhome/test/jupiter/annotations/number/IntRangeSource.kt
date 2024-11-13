package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.IntRangeDataProvider

/**
 * Annotation to indicate that the annotated int parameter should be populated with an integer range
 * from [min] to [max] with an [increment] step in the [ascending] direction.
 *
 * @sample examples.IntRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(IntRangeDataProvider::class)
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
