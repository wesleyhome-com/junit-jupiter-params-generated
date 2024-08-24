package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.DoubleSource
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.Test

class DoubleValueSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<DoubleValueSourceDataProvider, Double, DoubleSource>() {

    @Test
    fun testCreateParameterOptionsData() {
        val (values, testParameter) = parameterPair()
        testCreateParameterOptionsData(testParameter, false) {
            it.containsExactlyElementsOf(values.toList())
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        return parameterPair().second
    }

    private fun parameterPair(): Pair<DoubleArray, TestParameter> {
        val values = (1.0..10.0 step .5).toList().toDoubleArray()
        val testParameter = createAnnotatedTestParameter(values)
        return Pair(values, testParameter)
    }
}
