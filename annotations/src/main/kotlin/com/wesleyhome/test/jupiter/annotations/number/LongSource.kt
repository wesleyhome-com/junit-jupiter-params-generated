package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated long parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.LongSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class LongSource(val values: LongArray)
