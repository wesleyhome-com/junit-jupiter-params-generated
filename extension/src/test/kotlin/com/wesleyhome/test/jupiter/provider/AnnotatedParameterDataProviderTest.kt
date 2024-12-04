package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.typeArguments
import com.wesleyhome.test.jupiter.kotlinType
import kotlin.reflect.KClass

internal abstract class AnnotatedParameterDataProviderTest<P : AbstractAnnotatedParameterDataProvider<T, A>, T : Any, A : Annotation> :
    AbstractParameterDataProviderTest<P, T>() {
    private val annotationType: KClass<A> by lazy { typeArguments[2].kotlinType() }

    protected open fun createAnnotatedTestParameter(vararg annotationParameters: Any?): TestParameter {
        val annotation = createAnnotation(*annotationParameters)
        return TestParameter(
            name = "testCreateParameterOptionsData",
            type = parameterType,
            isNullable = true,
            annotations = listOf(annotation)
        )

    }

    protected fun createAnnotation(vararg annotationParameters: Any?): A {
        val constructors = annotationType.constructors
        return constructors.first().call(*annotationParameters)
    }

    protected fun createAnnotatedTestParameter(annotation: A): TestParameter {
        return TestParameter(
            name = "testCreateParameterOptionsData",
            type = parameterType,
            isNullable = true,
            annotations = listOf(annotation)
        )
    }

}
