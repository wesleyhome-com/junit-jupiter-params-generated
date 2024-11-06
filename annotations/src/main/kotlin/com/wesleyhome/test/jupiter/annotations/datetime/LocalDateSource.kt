package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to indicate that the annotated LocalDate parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.LocalDateSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LocalDateSource(
    /**
     * The array of possible values to use for the random date.
     * @return the array of possible values
     * @see java.time.LocalDate.parse
     * @see java.time.format.DateTimeFormatter
     */
    val values: Array<String>,

    /**
     * The format to use when parsing the date string.
     * @return the date format
     * @see java.time.format.DateTimeFormatter
     */
    val dateFormat: String = "yyyy-MM-dd"
)
