package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.number.FloatValueSourceDataProvider

/**
 * Annotation to indicate that the annotated float parameter should be populated with a random value from the provided
 * [values] array.
 *
 * @sample examples.FloatSource.example
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(FloatValueSourceDataProvider::class)
@MustBeDocumented
annotation class FloatSource(val values: FloatArray)
