package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.IntValueSourceDataProvider

/**
 * Annotation to indicate that the annotated int parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.IntSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(IntValueSourceDataProvider::class)
@MustBeDocumented
annotation class IntSource(val values: IntArray)
