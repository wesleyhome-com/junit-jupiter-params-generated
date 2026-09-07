package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.resolveTypeArgument
import kotlin.reflect.KClass

/**
 * Base for a provider driven by a parameter annotation.
 *
 * Extends [AbstractParameterDataProvider] with the annotation type [A], so the provider claims a
 * parameter only when it is of type [T] *and* carries [A]. Read the annotation off the parameter
 * with [findAnnotation].
 *
 * ```kotlin
 * @Target(AnnotationTarget.VALUE_PARAMETER)
 * @Retention(AnnotationRetention.RUNTIME)
 * @SourceProvider(EvenSourceDataProvider::class)
 * annotation class EvenSource(val max: Int)
 *
 * class EvenSourceDataProvider : AbstractAnnotatedParameterDataProvider<Int, EvenSource>() {
 *     override fun createParameterOptionsData(testParameter: TestParameter): List<Int> =
 *         (0..findAnnotation(testParameter)!!.max step 2).toList()
 * }
 * ```
 */
abstract class AbstractAnnotatedParameterDataProvider<T : Any, A : Annotation> :
    AbstractParameterDataProvider<T>() {

    protected val annotation: KClass<A> by lazy {
        resolveTypeArgument(AbstractAnnotatedParameterDataProvider::class.java, 1)
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
