package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.KClass

data class TestModel(
    val testParameters: List<TestParameter>,
)

data class TestParameter(
    val name: String,
    val type: KClass<*>,
    val isNullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()
)
