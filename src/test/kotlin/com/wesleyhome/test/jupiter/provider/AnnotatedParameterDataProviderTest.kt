package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.actualTypeArguments
import com.wesleyhome.test.jupiter.kotlinType
import kotlin.reflect.KClass

abstract class AnnotatedParameterDataProviderTest<P : AbstractAnnotatedParameterDataProvider<T, A>, T : Any, A : Annotation> : AbstractParameterDataProviderTest<P, T>() {
    private val annotationType: KClass<A> by lazy { actualTypeArguments[2].kotlinType() }

    protected fun createAnnotatedTestParameter(vararg annotationParameters: Any): TestParameter {
        val constructors = annotationType.constructors
        val annotation = constructors.first().call(*annotationParameters)
        return TestParameter(
            name = "testCreateParameterOptionsData",
            type = parameterType,
            isNullable = true,
            annotations = listOf(annotation)
        )

    }

}
