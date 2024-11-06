package com.wesleyhome.test.jupiter.annotations.datetime

/**
 * Annotation to indicate that the annotated LocalDate parameter should be populated with a LocalDate range
 * from [min] to [max] with an [increment] step in the [ascending] direction. The default [increment] is 1 day.
 * The default [ascending] is true.
 *
 * @sample examples.LocalDateRangeSource.example
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
