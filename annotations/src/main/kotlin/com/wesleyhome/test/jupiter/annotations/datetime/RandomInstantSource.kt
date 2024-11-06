package com.wesleyhome.test.jupiter.annotations.datetime

import java.time.temporal.ChronoUnit

/**
 * Annotation to be utilized on a parameter of type Instant in a parameterized test. The annotated parameter
 * value will be populated with a randomized value derived from the provided [minInstant], [maxInstant], [minOffset], and [maxOffset].
 * This will generate [size] number of random values with in the range specified.
 *
 * If provided, the [minInstant] and [maxInstant] properties will take precedent over any value provided by [minOffset] and [maxOffset]
 *
 * The [minInstant] and [maxInstant] properties should be parsable by [java.time.Instant.parse].
 * The [minOffset] and [maxOffset] properties should be parsable by [java.time.Period.parse].
 *
 * [minInstant], [maxInstant], [minOffset], and [maxOffset] properties are inclusive.
 *
 * If neither [minInstant] or [minOffset] are provided, an error will be thrown.
 * If [minInstant] is provided, [maxInstant] must also be provided.
 * If [minOffset] is provided, [maxOffset] must also be provided.
 *
 * The [minOffset] and [maxOffset] properties are relative to the current time when the test is run.
 * For example, if the current time is 2023-01-01T00:00:00.000Z, and [minOffset] is "-P1D" (one day before), the minimum Instant value will be 2022-12-31T00:00:00.000Z.
 * If [maxOffset] is "P1D" (one day after), the maximum Instant value will be 2023-01-02T00:00:00.000Z.
 *
 * @sample examples.RandomInstantSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class RandomInstantSource(
    /**
     * The number of random values to generate
     */
    val size: Int,
    /**
     * The minimum instant value to be generated. The value is inclusive.
     * This overrides any value by [minOffset]
     */
    val minInstant: String = "",
    /**
     * The maximum instant value to be generated. The value is exclusive.
     * This overrides any value by [maxOffset]
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
     * used if [minOffset] is provided.
     */
    val truncateTo: TruncateChronoUnit = TruncateChronoUnit.MINUTES,
)

/**
 * Valid [ChronoUnit] values to use with [RandomInstantSource.truncateTo]
 */
enum class TruncateChronoUnit (val chronoUnit: ChronoUnit) {
    NANOS(ChronoUnit.NANOS),
    MICROS(ChronoUnit.MICROS),
    MILLIS(ChronoUnit.MILLIS),
    SECONDS(ChronoUnit.SECONDS),
    MINUTES(ChronoUnit.MINUTES),
    HOURS(ChronoUnit.HOURS),
    HALF_DAYS(ChronoUnit.HALF_DAYS),
    DAYS(ChronoUnit.DAYS),
}
