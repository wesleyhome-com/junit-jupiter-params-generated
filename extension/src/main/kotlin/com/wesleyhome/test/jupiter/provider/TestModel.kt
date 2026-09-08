package com.wesleyhome.test.jupiter.provider

import java.time.Clock
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
 * @property clock The clock a time-based provider should read "now" from.
 */
data class TestParameter @JvmOverloads constructor(
    val name: String,
    val type: KClass<*>,
    val isNullable: Boolean = false,
    val annotations: List<Annotation> = emptyList(),
    val clock: Clock = Clock.systemUTC()
)
