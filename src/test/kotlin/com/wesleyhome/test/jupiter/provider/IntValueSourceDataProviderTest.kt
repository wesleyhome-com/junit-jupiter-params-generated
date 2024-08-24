package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.IntSource
import org.junit.jupiter.api.Test

class IntValueSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<IntValueSourceDataProvider, Int, IntSource>() {

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

    private fun parameterPair(): Pair<IntArray, TestParameter> {
        val values = (1..10 step 1).toList().toIntArray()
        val testParameter = createAnnotatedTestParameter(values)
        return Pair(values, testParameter)
    }
}
