package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.actualTypeArguments
import com.wesleyhome.test.jupiter.kotlinType
import kotlin.reflect.KClass

abstract class AbstractAnnotatedParameterDataProvider<T : Any, A : Annotation> : AbstractParameterDataProvider<T>() {

    protected val annotation: KClass<A> by lazy { actualTypeArguments[1].kotlinType() }

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    protected fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it.annotationClass == annotation }.let {
            if (it == null) null else it as A
        }
}
