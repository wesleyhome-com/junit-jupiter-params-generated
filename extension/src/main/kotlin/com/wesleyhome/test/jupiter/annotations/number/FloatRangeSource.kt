package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.FloatRangeDataProvider

/**
 * Annotation to indicate that the annotated float parameter should be populated with a float range
 * from [min] to [max] with an [increment] step in the [ascending] direction.
 *
 * @sample examples.FloatRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(FloatRangeDataProvider::class)
@MustBeDocumented
annotation class FloatRangeSource(
    /**
     * The minimum value of the range.
     */
    val min: Float,
    /**
     * The maximum value of the range.
     */
    val max: Float,
    /**
     * The increment step between values in the range.
     * The default value 0.5f
     */
    val increment: Float = 0.5f,
    /**
     * Whether the range should be in ascending or descending order.
     * The default value true
     */
    val ascending: Boolean = true
)
