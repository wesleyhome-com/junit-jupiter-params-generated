package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.datetime.InstantRangeSourceDataProvider

/**
 * Annotation to indicate that the annotated Instant parameter should be populated with an Instant range
 * from [minInstant] up to [maxInstant] or [minOffset] up to [maxOffset] with an [increment]. The default [increment] is 1 hour.
 * If [ascending] is false, the process will be reversed.
 * The default [ascending] is true.
 *
 * @sample examples.InstantRangeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(InstantRangeSourceDataProvider::class)
@MustBeDocumented
annotation class InstantRangeSource(
    /**
     * The minimum instant value that the range of values will include. Depending on [increment]
     * and [ascending], this value may or may not be used as a parameter value.
     *
     * This overrides [minOffset] if provided.
     */
    val minInstant: String = "",
    /**
     * The maximum instant value that the range of values will include. Depending on [increment]
     * and [ascending], this value may or may not be used as a parameter value.
     *
     * This overrides [maxOffset] if provided.
     */
    val maxInstant: String = "",
    /**
     * The offset for the minimum Instant value from [java.time.Instant.now]. For example -P1D.
     * This need to be parsable by [java.time.Period].
     * This is overridden by [minInstant] if provided.
     */
    val minOffset: String = "",
    /**
     * The offset for the maximum Instant value from [java.time.Instant.now]. For example P1D.
     * This need to be parsable by [java.time.Period].
     * This is overridden by [maxInstant] if provided.
     */
    val maxOffset: String = "",
    /**
     * The truncation unit that the starting instant will be truncated to. This is only
     * used if [minOffset] is provided. This must be a value of [java.time.temporal.ChronoUnit].
     */
    val truncateTo: TruncateChronoUnit = TruncateChronoUnit.MINUTES,
    /**
     * How much time to increment the value for each test. If [ascending] is false, this value will be
     * negated. The default value is 1 hour. Should follow the ISO-8601 duration format e.g. "PT1h" for
     * 1 hour, "PT30m" for 30 minutes.
     */
    val increment: String = "PT1h",
    /**
     * The direction the values will be generated. If true, the range will start with [minInstant]
     * and increment until it reaches [maxInstant]. If false, the range will start with [maxInstant]
     * and decrement until it reaches [minInstant].
     */
    val ascending: Boolean = true
)
