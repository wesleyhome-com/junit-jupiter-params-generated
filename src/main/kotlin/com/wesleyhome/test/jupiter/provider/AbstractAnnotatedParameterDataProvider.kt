package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.KClass

abstract class AbstractAnnotatedParameterDataProvider<T : Any, A : Annotation> : AbstractParameterDataProvider<T>() {

    abstract val annotation: KClass<A>

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    protected fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it.annotationClass == annotation }.let {
            if(it == null) null else it as A
        }
}
