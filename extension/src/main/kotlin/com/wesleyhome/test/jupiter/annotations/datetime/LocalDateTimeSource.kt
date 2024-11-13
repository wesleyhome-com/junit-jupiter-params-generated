package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.datetime.LocalDateTimeValueSourceDataProvider

/**
 * Annotation to indicate that the annotated LocalDateTime parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.LocalDateTimeSource.example
 **/
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(LocalDateTimeValueSourceDataProvider::class)
@MustBeDocumented
annotation class LocalDateTimeSource(
    /**
     * An array of strings representing the possible values for the LocalDateTime.
     * The values should be in the format specified by the [dateTimeFormat] parameter.
     *
     * @see dateTimeFormat
     */
    val values: Array<String>,

    /**
     * The format string used to parse the date and time strings in the [values] array.
     * The default value is "yyyy-MM-dd HH:mm", which represents the format "2023-05-15 14:30".
     *
     *
     * @see values
     * @see java.time.LocalDateTime.parse
     * @see java.time.format.DateTimeFormatter
     */
    val dateTimeFormat: String = "yyyy-MM-dd HH:mm"
)
