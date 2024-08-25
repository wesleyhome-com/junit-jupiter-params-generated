package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.InstantSource
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Instant

class InstantValueSourceAbstractParameterDataProviderTest :
    AnnotatedParameterDataProviderTest<InstantValueSourceDataProvider, Instant, InstantSource>() {

    @GeneratedParametersTest
    fun testCreateParameterOptionsData(isNullable: Boolean) {
        val (instants, testParameter) = parameterPair()
        testCreateParameterOptionsData(testParameter, isNullable) {
            it.containsExactlyElementsOf(instants)
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        return parameterPair().second
    }

    private fun parameterPair(): Pair<List<Instant>, TestParameter> {
        val instants = (0 until 10).map {
            Instant.now().plusSeconds(it.toLong())
        }
        val times = instants.map { it.toString() }.toTypedArray()
        val testParameter = createAnnotatedTestParameter(times)
        return Pair(instants, testParameter)
    }

}
