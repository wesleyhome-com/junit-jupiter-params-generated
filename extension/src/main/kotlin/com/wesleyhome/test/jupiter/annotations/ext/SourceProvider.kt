package com.wesleyhome.test.jupiter.annotations.ext

import kotlin.reflect.KClass

/**
 * Annotation to specify the source provider for a Data Annotation
 *
 * @property value the source provider class
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SourceProvider(
    val value: KClass<*>
)
