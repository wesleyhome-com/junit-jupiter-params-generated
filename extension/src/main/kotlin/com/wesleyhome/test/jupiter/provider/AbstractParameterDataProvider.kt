package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.typeArguments
import com.wesleyhome.test.jupiter.kotlinType
import kotlin.reflect.KClass

internal abstract class AbstractParameterDataProvider<T : Any> : ParameterDataProvider<T> {
    private val classType: KClass<T> by lazy {
        val arguments = typeArguments
        arguments[0].kotlinType()
    }
    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return testParameter.type == dataProviderFor()
    }

    private fun dataProviderFor(): KClass<T> {
        return classType
    }
}
