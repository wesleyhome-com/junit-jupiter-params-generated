package com.wesleyhome.test.jupiter.annotations

/**
 * Annotation to indicate that the annotated String parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.StringSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StringSource(val values: Array<String>)
