package com.wesleyhome.test.jupiter.annotations.number

/**
 * Annotation to indicate that the annotated double parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.DoubleSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DoubleSource(val values: DoubleArray)
