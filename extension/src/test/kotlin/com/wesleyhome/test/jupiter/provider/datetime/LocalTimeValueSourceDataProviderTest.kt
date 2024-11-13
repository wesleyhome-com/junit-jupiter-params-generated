package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeSource
import com.wesleyhome.test.jupiter.formatter
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.step
import com.wesleyhome.test.jupiter.toLocalTime
import org.junit.jupiter.api.Test
import java.time.LocalTime


class LocalTimeValueSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<LocalTimeValueSourceDataProvider, LocalTime, LocalTimeSource>() {

    @Test
    fun testCreateParameterOptionsData() {
        val (values, testParameter) = parameterPair()
        testCreateParameterOptionsData(testParameter, false) {
            it.containsExactlyElementsOf(values.map { ldString -> ldString.toLocalTime(DATE_FORMAT) }.toList())
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        val (_, testParameter) = parameterPair()
        return testParameter
    }

    private fun parameterPair(): Pair<Array<String>, TestParameter> {
        val from = "05:30".toLocalTime(DATE_FORMAT)
        val to = "15:30".toLocalTime(DATE_FORMAT)
        val values = (from..to step "PT1h").map { it.format(DATE_FORMAT.formatter()) }.toTypedArray()
        val testParameter = createAnnotatedTestParameter(values, DATE_FORMAT)
        return Pair(values, testParameter)
    }

    companion object {
        private const val DATE_FORMAT = "HH:mm"
    }
}
