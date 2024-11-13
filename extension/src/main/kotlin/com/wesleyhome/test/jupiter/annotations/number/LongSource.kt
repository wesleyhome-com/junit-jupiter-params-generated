package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.LongValueSourceDataProvider

/**
 * Annotation to indicate that the annotated long parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.LongSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(LongValueSourceDataProvider::class)
@MustBeDocumented
annotation class LongSource(val values: LongArray)
