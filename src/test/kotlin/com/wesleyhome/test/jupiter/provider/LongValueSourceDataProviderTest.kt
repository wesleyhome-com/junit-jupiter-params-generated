package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LongSource
import org.junit.jupiter.api.Test

class LongValueSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<LongValueSourceDataProvider, Long, LongSource>() {

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

    private fun parameterPair(): Pair<LongArray, TestParameter> {
        val values = (1L..10L step 1L).toList().toLongArray()
        val testParameter = createAnnotatedTestParameter(values)
        return Pair(values, testParameter)
    }
}
