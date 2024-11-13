package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.LongRangeDataProvider

/**
 * Annotation to indicate that the annotated long parameter should be populated with a long range
 * from [min] to [max] with an [increment] step in the [ascending] direction.
 *
 * @sample examples.LongRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(LongRangeDataProvider::class)
@MustBeDocumented
annotation class LongRangeSource(
    /**
     * The minimum value in the range.
     */
    val min: Long,
    /**
     * The maximum value in the range.
     */
    val max: Long,
    /**
     * The increment value for the range.
     */
    val increment: Long = 1,
    /**
     * Whether the range should be in ascending or descending order.
     */
    val ascending: Boolean = true
)
