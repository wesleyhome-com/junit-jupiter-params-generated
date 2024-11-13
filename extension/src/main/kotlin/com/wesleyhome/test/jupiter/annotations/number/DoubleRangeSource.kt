package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.DoubleRangeDataProvider

/**
 * Annotation to indicate that the annotated double parameter should be populated with a double range
 * from [min] to [max] with an [increment] step in the [ascending] direction.
 *
 * @sample examples.DoubleRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(DoubleRangeDataProvider::class)
@MustBeDocumented
annotation class DoubleRangeSource(
    /**
     * The minimum value for the range.
     */
    val min: Double,
    /**
     * The maximum value for the range.
     */
    val max: Double,
    /**
     * The increment value for the range. The default value is 0.5
     */
    val increment: Double = 0.5,
    /**
     * Whether the range should be ascending or descending.
     */
    val ascending: Boolean = true
)
