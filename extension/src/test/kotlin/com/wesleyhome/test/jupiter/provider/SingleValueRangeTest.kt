package com.wesleyhome.test.jupiter.provider

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import org.junit.jupiter.api.AfterAll
import java.time.LocalDate

/**
 * A range whose bounds are equal holds exactly one value. Rejecting it forced callers to fake a
 * wider range, or to drop back to an explicit value source, to test a single boundary.
 */
class SingleValueRangeTest {

    @GeneratedParametersTest
    fun testNumberRangeWithEqualBounds(@IntRangeSource(min = 5, max = 5) value: Int) {
        ints += value
    }

    @GeneratedParametersTest
    fun testDateRangeWithEqualBounds(
        @LocalDateRangeSource(min = "2024-01-01", max = "2024-01-01") value: LocalDate
    ) {
        dates += value
    }

    companion object {
        private val ints = mutableListOf<Int>()
        private val dates = mutableListOf<LocalDate>()

        @JvmStatic
        @AfterAll
        fun assertSingleValueRanges() {
            assertThat(ints).isEqualTo(listOf(5))
            assertThat(dates).isEqualTo(listOf(LocalDate.of(2024, 1, 1)))
        }
    }
}
