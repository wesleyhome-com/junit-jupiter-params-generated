package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to be utilized on a parameter of type Instant in a parameterized test. The annotated parameter
 * value will be populated with a randomized value derived from the provided [minInstant], [maxInstant], [minOffset], and [maxOffset].
 * This will generate [size] number of random values with in the range specified.
 *
 * If provided, the [minInstant] and [maxInstant] properties will take precedent oven any value provided by [minOffset] and [maxOffset]
 *
 * The [minInstant] and [maxInstant] properties should be parsable by [java.time.Instant.parse].
 * The [minOffset] and [maxOffset] properties should be parsable by [java.time.Period.parse].
 *
 * The [minOffset] and [maxOffset] properties are relative to the current time when the test is run.
 * For example, if the current time is 2023-01-01T00:00:00.000Z, and [minOffset] is "-P1D" (one day before), the minimum Instant value will be 2022-12-31T00:00:00.000Z.
 * If [maxOffset] is "P1D" (one day after), the maximum Instant value will be 2023-01-02T00:00:00.000Z.
 *
 * For instance:
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@RandomInstantSource(size = 10, minInstant = "2023-01-01T00:00:00.000Z", maxInstant = "2023-01-02T01:00:00.000Z") instant: Instant) {
 *     // test code
 *     }
 *     // the above will generate 1 individual test, with 'instant' parameter set to a random Instant value between 2023-01-01T00:00:00.000Z and 2023-01-01T01:00:00.000Z
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@RandomInstantSource(size = 10, startPeriodOffset = "-P1D", endPeriodOffset = "P1D") instant: Instant) {
 *     // test code
 *     }
 *     // the above will generate 1 individual test, with 'instant' parameter set to a random Instant value between 1 day before the current time and 1 day after the current time
 *
 * </code>
 *
 * @property size The number of random values to generate
 * @property minInstant The minimum instant value to be generated. The value is inclusive.
 * @property maxInstant The maximum instant value to be generated. The value is exclusive.
 * @property minOffset The offset for the minimum Instant value from [java.time.Instant.now]. For example -P1D.
 * @property maxOffset The offset for the maximum Instant value from [java.time.Instant.now]. For example P1D.
 *
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
     * used if [minOffset] is provided. This must be a value of [java.time.temporal.ChronoUnit].
     */
    val truncateTo: String = "MINUTES",
)
