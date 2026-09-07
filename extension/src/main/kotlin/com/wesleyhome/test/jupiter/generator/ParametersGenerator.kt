package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.generator.DataProviderRegistry.createInstance
import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.params.provider.Arguments
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

internal class ParametersGenerator(
    testModel: TestModel
) {

    /**
     * The generated parameters, paired with the index of the test method parameter they belong to.
     *
     * Parameters this extension does not generate values for are absent, leaving them to whatever
     * other [org.junit.jupiter.api.extension.ParameterResolver] is registered for the test.
     */
    private val generated: List<Pair<Int, List<Any?>>> by lazy {
        testModel.testParameters.mapIndexedNotNull { index, testParameter ->
            optionsFor(testParameter)?.let { index to it }
        }
    }

    /** Indices of the test method parameters this generator supplies values for, in declaration order. */
    val parameterIndices: List<Int>
        get() = generated.map { it.first }

    fun arguments(): Iterable<Arguments> = ArgumentParameters(generated.map { it.second })

    /**
     * Returns the values to generate for [testParameter], or `null` when the parameter is not ours.
     *
     * A parameter carrying an explicit source annotation is always ours: if no provider accepts it the
     * annotation is misapplied and that is reported. A parameter with no source annotation that none of
     * the default providers handle belongs to another resolver, not to us.
     */
    private fun optionsFor(testParameter: TestParameter): List<Any?>? {
        val annotatedProviders = testParameter.annotations.mapNotNull { annotation ->
            val annotationClass = annotation.annotationClass
            val sourceProvider: SourceProvider = annotationClass.findAnnotation() ?: return@mapNotNull null
            val providerClass: KClass<*> = sourceProvider.value
            if (!providerClass.isSubclassOf(ParameterDataProvider::class)) {
                throw InvalidParameterException(testParameter)
            }
            @Suppress("UNCHECKED_CAST")
            createInstance(annotationClass.qualifiedName!!, providerClass as KClass<ParameterDataProvider<Any>>)
        }
        val providers = annotatedProviders
            .ifEmpty { DataProviderRegistry.defaultDataProviders }
            .filter { it.providesDataFor(testParameter) }
        if (providers.isEmpty()) {
            if (annotatedProviders.isEmpty()) {
                return null
            }
            throw InvalidParameterException(testParameter)
        }
        val values = providers.flatMap { it.createParameterOptionsData(testParameter) }
        return if (testParameter.isNullable) values + null else values
    }
}
