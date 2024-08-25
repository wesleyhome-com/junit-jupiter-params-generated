package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.annotations.DataProviderRegistry.dataProviders
import com.wesleyhome.test.jupiter.annotations.DataProviderRegistry.defaultDataProviders
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.params.provider.Arguments

class ParametersGenerator(
    testModel: TestModel
) {

    private val options: List<List<Any?>> by lazy {
        testModel.testParameters.map { testParameter: TestParameter ->
            dataProviders.filter {
                it.providesDataFor(testParameter)
            }.ifEmpty {
                val p = defaultDataProviders
                p.filter {
                    it.providesDataFor(testParameter)
                }.take(1)
            }.ifEmpty {
                throw InvalidParameterException(testParameter)
            }.flatMap {
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
