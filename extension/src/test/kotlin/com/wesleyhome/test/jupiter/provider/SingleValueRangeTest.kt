package com.wesleyhome.test.jupiter.provider

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * A range whose bounds are equal holds exactly one value. Rejecting it forced callers to fake a
 * wider range, or to drop back to an explicit value source, to test a single boundary.
 */
class SingleValueRangeTest {

    @Test
    fun testNumberRangeWithEqualBounds() {
        assertThat(invocationNames(Fixture::class.java, "numberRangeWithEqualBounds"))
            .isEqualTo(listOf("5"))
    }

    @Test
    fun testDateRangeWithEqualBounds() {
        assertThat(invocationNames(Fixture::class.java, "dateRangeWithEqualBounds"))
            .isEqualTo(listOf(LocalDate.of(2024, 1, 1).toString()))
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun numberRangeWithEqualBounds(@IntRangeSource(min = 5, max = 5) value: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun dateRangeWithEqualBounds(
            @LocalDateRangeSource(min = "2024-01-01", max = "2024-01-01") value: LocalDate
        ) {
        }
    }
}
