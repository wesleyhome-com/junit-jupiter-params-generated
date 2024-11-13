package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateSource
import com.wesleyhome.test.jupiter.formatter
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.step
import com.wesleyhome.test.jupiter.toLocalDate
import org.junit.jupiter.api.Test
import java.time.LocalDate


class LocalDateValueSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<LocalDateValueSourceDataProvider, LocalDate, LocalDateSource>() {

    @Test
    fun testCreateParameterOptionsData() {
        val (values, testParameter) = parameterPair()
        testCreateParameterOptionsData(testParameter, false) {
            it.containsExactlyElementsOf(values.map { ldString -> ldString.toLocalDate(DATE_FORMAT) }.toList())
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        val (_, testParameter) = parameterPair()
        return testParameter
    }

    private fun parameterPair(): Pair<Array<String>, TestParameter> {
        val from = "2024-01-01".toLocalDate(DATE_FORMAT)
        val to = "2024-02-01".toLocalDate(DATE_FORMAT)
        val values = (from..to step "P1d").map { it.format(DATE_FORMAT.formatter()) }.toTypedArray()
        val testParameter = createAnnotatedTestParameter(values, DATE_FORMAT)
        return Pair(values, testParameter)
    }

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }
}
