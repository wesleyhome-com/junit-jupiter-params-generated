package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.FloatSource
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.Test

class FloatValueSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<FloatValueSourceDataProvider, Float, FloatSource>() {

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

    private fun parameterPair(): Pair<FloatArray, TestParameter> {
        val values = (1f..10f step 1f).toList().toFloatArray()
        val testParameter = createAnnotatedTestParameter(values)
        return Pair(values, testParameter)
    }
}
