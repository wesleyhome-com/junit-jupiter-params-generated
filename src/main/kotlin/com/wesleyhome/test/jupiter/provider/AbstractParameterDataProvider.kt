package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.actualTypeArguments
import com.wesleyhome.test.jupiter.kotlinType
import kotlin.reflect.KClass

abstract class AbstractParameterDataProvider<T : Any> : ParameterDataProvider<T> {
    private val classType: KClass<T> by lazy { actualTypeArguments[0].kotlinType()}

    override fun dataProviderFor(): KClass<T> {
        return classType
    }
}
