package com.wesleyhome.test.jupiter.annotations

/**
 * Annotation to indicate that the annotated int parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@IntSource([1, 2, 3]) value: Int) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1, 2, and 3
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntSource(val values: IntArray)

/**
 * Annotation to indicate that the annotated long parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LongSource([1, 2, 3]) value: Long) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1, 2, and 3
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LongSource(val values: LongArray)

/**
 * Annotation to indicate that the annotated double parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@DoubleSource([1.0, 2.0, 3.0]) value: Double) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1.0, 2.0, and 3.0
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleSource(val values: DoubleArray)

/**
 * Annotation to indicate that the annotated float parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@FloatSource([1.0, 2.0, 3.0]) value: Float) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values 1.0, 2.0, and 3.0
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class FloatSource(val values: FloatArray)

/**
 * Annotation to indicate that the annotated LocalDate parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@StringSource(["2023-01-01", "2022-01-01", "2021-01-01"]) value: String) {
 *     // test code
 *     }
 *     // will generate 3 tests with the date values January 1st, 2023, January 1st, 2022, and January 1st, 2021
 *     // the values will be used in the order they are provided
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateSource(values = ["01/01/2023", "02/01/2023", "03/01/2023"], dateFormat = "MM/dd/yyyy") value: LocalDate) {
 *     // test code
 *     }
 *     // will generate 3 tests with the date values January 1st, 2023, February 1st, 2023, and March 1st, 2023
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateSource(val values: Array<String>, val dateFormat: String = "yyyy-MM-dd")

/**
 * Annotation to indicate that the annotated LocalDateTime parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@StringSource(["2023-01-01 00:00", "2022-01-01 00:00", "2021-01-01 00:00"]) value: String) {
 *     // test code
 *     }
 *     // will generate 3 tests with the date values January 1st, 2023, January 1st, 2022, and January 1st, 2021
 *     // the values will be used in the order they are provided
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateTimeSource(values = ["01/01/2023 00:00", "02/01/2023 00:00", "03/01/2023 00:00"], dateTimeFormat = "MM/dd/yyyy HH:mm") value: LocalDateTime) {
 *     // test code
 *     }
 *     // will generate 3 tests with the date values January 1st, 2023, February 1st, 2023, and March 1st, 2023
 *     // the values will be used in the order they are provided
 * </code>
 **/
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateTimeSource(val values: Array<String>, val dateTimeFormat: String = "yyyy-MM-dd HH:mm")

/**
 * Annotation to be utilized on a parameter of type LocalTime in a parametrized test. The annotated parameter's
 * value will be populated with a randomized value derived from the provided [values] array.
 *
 * The [values] array should consist of time string values, formatted according to the [timeFormat] property.
 *
 * Default value for [timeFormat] is "HH:mm:ss".
 *
 * For instance:
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalTimeSource(values = ["12:00", "14:30", "16:45"]) time: LocalTime) {
 *     // test code
 *     }
 *     // the above will generate 3 individual tests, with 'time' parameter set to 12:00:00, 14:30:00, and 16:45:00 respectively
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalTimeSource(values = ["12:00:00", "14:30:00", "16:45:01"], timeFormat = "HH:mm:ss) time: LocalTime) {
 *     // test code
 *     }
 *     // the above will generate 3 individual tests, with 'time' parameter set to 12:00:00, 14:30:00, and 16:45:01 respectively
 *
 * </code>
 *
 * @property values An array of string representing time, to be converted into LocalTime instances.
 * @property timeFormat A String representing the pattern to be used for parsing the [values] into LocalTime instances.
*/
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalTimeSource(val values: Array<String>, val timeFormat: String = "HH:mm")

/**
 * Annotation to indicate that the annotated int parameter should be populated with an integer range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@IntRangeSource(min = 1, max = 300) value: Int) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in ascending order
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@IntRangeSource(min = 1, max = 300, increment = 1, ascending = false) value: Int) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in descending order
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IntRangeSource(val min: Int, val max: Int, val increment: Int = 1, val ascending: Boolean = true)

/**
 * Annotation to indicate that the annotated long parameter should be populated with a long range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LongRangeSource(min = 1, max = 300) value: Long) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in ascending order
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LongRangeSource(min = 1, max = 300, increment = 1, ascending = false) value: Long) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1 to 300
 *     // the values will be in descending order
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LongRangeSource(val min: Long, val max: Long, val increment: Long = 1, val ascending: Boolean = true)

/**
 * Annotation to indicate that the annotated double parameter should be populated with a double range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 0.5.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@DoubleRangeSource(min = 1.0, max = 300.0) value: Double) {
 *     // test code
 *     }
 *     // will generate 600 tests with the values 1.0 to 300.0
 *     // the values will be in ascending order
 *     // the values will be in increments of 0.5
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@DoubleRangeSource(min = 1.0, max = 300.0, increment = 1.0, ascending = false) value: Double) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1.0 to 300.0
 *     // the values will be in descending order
 *     // the values will be in increments of 1.0
 *  </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleRangeSource(
    val min: Double,
    val max: Double,
    val increment: Double = 0.5,
    val ascending: Boolean = true
)

/**
 * Annotation to indicate that the annotated float parameter should be populated with a float range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 0.5f.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@FloatRangeSource(min = 1.0f, max = 300.0f) value: Float) {
 *     // test code
 *     }
 *     // will generate 600 tests with the values 1.0 to 300.0
 *     // the values will be in ascending order
 *     // the values will be in increments of 0.5
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@FloatRangeSource(min = 1.0f, max = 300.0f, increment = 1.0f, ascending = false) value: Float) {
 *     // test code
 *     }
 *     // will generate 300 tests with the values 1.0 to 300.0
 *     // the values will be in descending order
 *     // the values will be in increments of 1.0
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class FloatRangeSource(
    val min: Float,
    val max: Float,
    val increment: Float = 0.5f,
    val ascending: Boolean = true
)

/**
 * Annotation to indicate that the annotated LocalDate parameter should be populated with a LocalDate range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1 day.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateRangeSource(min = "2021-01-01", max = "2021-01-31") value: LocalDate) {
 *     // test code
 *     }
 *     // will generate 31 tests with the values January 1st, 2021 to January 31st, 2021
 *     // the values will be in ascending order
 *     // the values will be in increments of 1 day
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(
 *          @LocalDateRangeSource(
 *              min = "01/01/2023",
 *              max = "02/01/2023",
 *              increment = "P2d",
 *              dateFormat = "MM/dd/yyyy",
 *              ascending = false
 *           )
 *           value: LocalDate
 *      ) {
 *     // test code
 *     }
 *     // will generate 16 tests with the values January 1st, 2023 to February 1st, 2023
 *     // the values will be in descending order
 *     // the values will be in increments of 2 days
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateRangeSource(
    val min: String,
    val max: String,
    val increment: String = "P1d",
    val ascending: Boolean = true,
    val dateFormat: String = "yyyy-MM-dd"
)

/**
 * Annotation to indicate that the annotated LocalDateTime parameter should be populated with a LocalDateTime range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1 hour.
 * The default [ascending] is true.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalDateTimeRangeSource(min = "2023-01-01 00:00", max = "2023-01-01 23:00") value: LocalDateTime) {
 *     // test code
 *     }
 *     // will generate 24 tests with the values January 1st, 2023 00:00 to January 1st, 2023 23:00
 *     // the values will be in ascending order
 *     // the values will be in increments of 1 hour
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(
 *          @LocalDateTimeRangeSource(
 *              min = "01/01/2023 00:00",
 *              max = "01/01/2023 23:00",
 *              increment = "PT2h",
 *              dateTimeFormat = "MM/dd/yyyy HH:mm",
 *              ascending = false
 *          )
 *          value: LocalDateTime
 *     ) {
 *     // test code
 *     }
 *     // will generate 12 tests with the values January 1st, 2023 00:00 to January 1st, 2023 23:00
 *     // the values will be in descending order
 *     // the values will be in increments of 2 hours
 *
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateTimeRangeSource(
    val min: String,
    val max: String,
    val increment: String = "PT1h",
    val ascending: Boolean = true,
    val dateTimeFormat: String = "yyyy-MM-dd HH:mm"
)

/**
 * Annotation to mark that the given LocalTime parameter should be populated with a LocalTime range
 * starting from the minimum value [min] to the maximum value [max] with an [increment] in the direction specified by [ascending].
 * The default [increment] is 1 hour and the default [ascending] order is true.
 *
 * Example of usage:
 * <code>
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@LocalTimeRangeSource(min = "12:00", max = "14:00", increment = "PT1h", ascending = true, dateTimeFormat = "HH:mm") time: LocalTime) {
 *         // test code
 *     }
 *     // This will generate 3 tests with the time values 12:00:00, 13:00:00, and 14:00:00
 *     // The values will be in 1 hour increments in ascending order
 * </code>
 *
 * @property min A string representing the minimum value for the LocalTime range. Should be specified in [timeFormat].
 * @property max A string representing the maximum value for the LocalTime range. Should be specified in [timeFormat].
 * @property increment A string representing the period of increment for the LocalTime range. Should follow the ISO-8601 duration format
 *                     e.g. "PT1h" for 1 hour, "PT30m" for 30 minutes. Default is "PT1h" for 1 hour.
 * @property ascending A boolean to indicate the order of the values in the LocalTime range. Default is true, ascending order.
 * @property timeFormat A string representing the format to be used for parsing [min] and [max] into LocalTime. Default is "HH:mm".
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalTimeRangeSource(
    val min: String,
    val max: String,
    val increment: String = "PT1h",
    val ascending: Boolean = true,
    val timeFormat: String = "HH:mm"
)

/**
 * Annotation to indicate that the annotated String parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@StringSource(["a", "b", "c"]) value: String) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values "a", "b", and "c"
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StringSource(val values: Array<String>)
