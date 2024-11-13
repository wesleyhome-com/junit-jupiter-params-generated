package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.datetime.LocalDateTimeValueSourceDataProvider

/**
 * Annotation to be utilized on a parameter of type LocalTime in a parametrized test. The annotated parameter's
 * value will be populated with a randomized value derived from the provided [values] array.
 *
 * The [values] array should consist of time string values, formatted according to the [timeFormat] property.
 *
 * Default value for [timeFormat] is "HH:mm".
 *
 *
 * @sample examples.LocalTimeSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(LocalDateTimeValueSourceDataProvider::class)
@MustBeDocumented
annotation class LocalTimeSource(
    /**
     * An array of string representing time, to be converted into LocalTime instances.
     * The time strings should be formatted according to the [timeFormat] property.
     *
     * @see timeFormat
     */
    val values: Array<String>,

    /**
     * A String representing the pattern to be used for parsing the [values] into LocalTime instances.
     * Default value is "HH:mm".
     *
     * @return Time format pattern string
     * @see java.time.format.DateTimeFormatter
     * @see java.time.LocalTime.parse
     *
     */
    val timeFormat: String = "HH:mm"
)
