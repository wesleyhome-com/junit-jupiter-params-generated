package com.wesleyhome.test.jupiter.annotations.ext

import kotlin.reflect.KClass

@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SourceProvider(
    val value: KClass<*>
)
