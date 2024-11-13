package com.wesleyhome.test.jupiter.annotations.datetime

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.datetime.InstantValueSourceDataProvider

/**
 * Annotation to be utilized on a parameter of type Instant in a parameterized test. The annotated parameter;s
 * value will be populated with a randomized value derived from the provided [values] array.
 *
 * The [values] array should consist of instant string values, formatted according to the [java.time.format.DateTimeFormatter.ISO_INSTANT]
 * standard.
 *
 * @sample examples.InstantSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(InstantValueSourceDataProvider::class)
@MustBeDocumented
annotation class InstantSource(
    /**
     * An array of string representing instant, to be converted into Instant instances.
     *
     * Values must be formatted according to the [java.time.format.DateTimeFormatter.ISO_INSTANT] standard.
     *
     * @see java.time.format.DateTimeFormatter.ISO_INSTANT
     * @see java.time.Instant
     */
    val values: Array<String>
)
