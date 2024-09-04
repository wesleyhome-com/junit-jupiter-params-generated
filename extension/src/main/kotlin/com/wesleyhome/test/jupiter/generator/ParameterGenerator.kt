package com.wesleyhome.test.jupiter.generator

import kotlin.reflect.KClass

interface ParameterGenerator {

    fun supportsType(type: KClass<*>): Boolean

    fun <T : Any> options(type: KClass<T>): List<T>
}
