package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.annotations.validation.datetime.TruncateChronoUnit
import com.wesleyhome.test.jupiter.provider.datetime.RandomInstanceSourceDataProvider

/**
 * This annotation is used to generate random [java.time.Instant]s.
 * The [min] and [max] values are used to determine the range of values to generate.
 * If [useOffset] is true, the [min] and [max] values are treated as offsets from the current time.
 * Otherwise, the [min] and [max] values are treated as [java.time.Instant]s.
 * The [size] parameter determines the number of random values to generate.
 * The [truncateTo] parameter determines the truncation unit that the starting instant will be truncated to.
 *
 * @sample examples.RandomInstantSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(RandomInstanceSourceDataProvider::class)
@MustBeDocumented
annotation class RandomInstantSource(
    /**
     * This minimum value that is used to generate random values.
     * If [useOffset] is true, this value needs to be parsable to a [java.time.Period]
     * Otherwise this value needs to be parseable to a [java.time.Instant]
     */
    val min: String = "",
    /**
     * The maximum value that is used to generate random values.
     * If [useOffset] is true, this value needs to be parsable to a [java.time.Period]
     * Otherwise this value needs to be parseable to a [java.time.Instant]
     */
    val max: String = "",
    /**
     * If true, the [min] and [max] values will be treated as offsets from the current time.
     * If false, the [min] and [max] values will be treated as [java.time.Instant]s.
     */
    val useOffset: Boolean = false,
    /**
     * The number of random values to generate
     */
    val size: Int,
    /**
     * The truncation unit that the starting instant will be truncated to. This is only
     * used if [minOffset] is provided.
     */
    val truncateTo: TruncateChronoUnit = TruncateChronoUnit.MINUTES,
)
