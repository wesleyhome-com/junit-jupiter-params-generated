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

    private val options: List<List<Any?>> by lazy {
        testModel.testParameters.map { testParameter: TestParameter ->
            val dataProviderList = testParameter.annotations.map { annotation ->
                val annotationClass = annotation.annotationClass
                val className = annotationClass.qualifiedName!!
                val sourceProvider: SourceProvider? = annotationClass
                    .findAnnotation<SourceProvider>()
                if (sourceProvider != null) {
                    val providerClass: KClass<*> = sourceProvider.value
                    if (providerClass.isSubclassOf(ParameterDataProvider::class)) {
                        createInstance(className, providerClass as KClass<ParameterDataProvider<Any?>>)
                    } else {
                        throw InvalidParameterException(testParameter)
                    }
                } else {
                    null
                }
            }.filterNotNull()
            dataProviderList
                .ifEmpty { DataProviderRegistry.defaultDataProviders }
                .filter { it.providesDataFor(testParameter) }
                .ifEmpty { throw InvalidParameterException(testParameter) }
                .flatMap {
                    it.createParameterOptionsData(testParameter)
                }.let { list ->
                    if (testParameter.isNullable) {
                        list + null
                    } else {
                        list
                    }
                }
        }
    }

    fun arguments(): Iterable<Arguments> {
        return ArgumentParameters(options)
    }
}
