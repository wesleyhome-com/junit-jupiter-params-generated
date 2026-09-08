package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.DEFAULT_MAX_PERMUTATIONS
import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.TooManyPermutationsException
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.generator.DataProviderRegistry.createInstance
import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Clock
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

internal class ParametersGenerator(
    private val testModel: TestModel,
    private val clock: Clock = Clock.systemUTC()
) {

    /**
     * The parameters this extension generates values for, in declaration order.
     *
     * Parameters it does not generate are absent, leaving them to whatever other
     * [org.junit.jupiter.api.extension.ParameterResolver] is registered for the test.
     */
    val parameters: List<GeneratedParameter> by lazy {
        testModel.testParameters.mapIndexedNotNull { index, declared ->
            val testParameter = declared.copy(clock = clock)
            optionsFor(testParameter)?.let { GeneratedParameter(index, testParameter.name, it) }
        }
    }

    val layout: GeneratedParameterLayout by lazy {
        GeneratedParameterLayout.of(parameters, testModel.testParameters.size)
    }

    fun arguments(maxPermutations: Long = DEFAULT_MAX_PERMUTATIONS): ArgumentParameters {
        val arguments = ArgumentParameters(parameters.map { it.options })
        if (arguments.totalPermutations > maxPermutations) {
            throw TooManyPermutationsException(
                parameters.map { it.name to it.options.size },
                arguments.totalPermutations,
                maxPermutations
            )
        }
        return arguments
    }

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

/** A test method parameter this extension generates values for, and the values it generates. */
internal data class GeneratedParameter(
    val index: Int,
    val name: String,
    val options: List<Any?>
)
