package com.wesleyhome.test.jupiter.generator

/**
 * One generated value, tied to the test method parameter it belongs to.
 *
 * [parameterIndex] is the position in the method signature, which is not the position among the
 * generated values once another extension owns some of the parameters.
 */
internal data class GeneratedArgument(
    val parameterIndex: Int,
    val name: String,
    val value: Any?
)
