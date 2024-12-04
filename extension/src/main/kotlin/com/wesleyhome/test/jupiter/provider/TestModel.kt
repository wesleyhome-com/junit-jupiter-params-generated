package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.KClass

internal data class TestModel(
    val testParameters: List<TestParameter>,
)

/**
 * Represents a parameter for a test method.
 *
 * @property name The name of the parameter.
 * @property type The type of the parameter.
 * @property isNullable Whether the parameter is nullable.
 * @property annotations The annotations associated with the parameter.
 */
data class TestParameter(
    val name: String,
    val type: KClass<*>,
    val isNullable: Boolean = false,
    val annotations: List<Annotation> = emptyList()
)
