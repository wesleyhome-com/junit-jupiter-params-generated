package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.datetime.InstantRangeSourceDataProvider

/**
 * Used to provide a range of instants to a test. The range is inclusive of the min and max values.
 *
 * @param min The minimum value for the range. Should follow the ISO-8601 instant format e.g. "2022-01-01T00:00:00Z"
 * @param max The maximum value for the range. Should follow the ISO-8601 instant format e.g. "2022-01-01T00:00:00Z"
 * @param increment How much time to increment the value for each test. If [ascending] is false, this value will be
 * negated. The default value is 1 hour. Should follow the ISO-8601 duration format e.g. "PT1h" for
 * 1 hour, "PT30m" for 30 minutes.
 * @param ascending The direction the values will be generated. If true, the range will start with [min]
 * and increment until it reaches [max]. If false, the range will start with [max]
 * and decrement until it reaches [min].
 *
 * @sample examples.RandomInstantSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(InstantRangeSourceDataProvider::class)
@MustBeDocumented
annotation class InstantRangeSource(
    /**
     * The minimum value for the range. Should follow the ISO-8601 instant format e.g. "2022-01-01T00:00:00Z"
     */
    val min: String,
    /**
     * The maximum value for the range. Should follow the ISO-8601 instant format e.g. "2022-01-01T00:00:00Z"
     */
    val max: String,
    /**
     * How much time to increment the value for each test. If [ascending] is false, this value will be
     * negated. The default value is 1 hour. Should follow the ISO-8601 duration format e.g. "PT1h" for
     * 1 hour, "PT30m" for 30 minutes.
     */
    val increment: String = "PT1h",
    /**
     * The direction the values will be generated. If true, the range will start with [min]
     * and increment until it reaches [max]. If false, the range will start with [max]
     * and decrement until it reaches [min].
     */
    val ascending: Boolean = true
)
