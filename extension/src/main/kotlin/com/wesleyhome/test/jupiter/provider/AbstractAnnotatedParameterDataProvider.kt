package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.typeArguments
import com.wesleyhome.test.jupiter.kotlinType
import kotlin.reflect.KClass

abstract class AbstractAnnotatedParameterDataProvider<T : Any, A : Annotation> : AbstractParameterDataProvider<T>() {

    protected val annotation: KClass<A> by lazy {
        val arguments = typeArguments
        arguments[1].kotlinType()
    }

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    protected fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it.annotationClass == annotation }.let {
            @Suppress("UNCHECKED_CAST")
            if (it == null) null else it as A
        }
}
