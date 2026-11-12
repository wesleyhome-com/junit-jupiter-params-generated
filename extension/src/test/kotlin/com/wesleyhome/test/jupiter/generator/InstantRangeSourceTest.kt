package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.InstantRangeSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantRangeSourceTest {

    @Test
    fun testRangeUsesTheDefaultHourlyIncrement() {
        assertThat(invocationNames(Fixture::class.java, "hourly"))
            .isEqualTo(listOf("2024-01-01T00:00:00Z", "2024-01-01T01:00:00Z", "2024-01-01T02:00:00Z"))
    }

    @Test
    fun testDescendingRangeWithACustomIncrement() {
        assertThat(invocationNames(Fixture::class.java, "descendingEveryHalfHour"))
            .isEqualTo(listOf("2024-01-01T01:00:00Z", "2024-01-01T00:30:00Z", "2024-01-01T00:00:00Z"))
    }

    @Test
    fun testRangeToMaxKeyword() {
        assertThat(invocationNames(Fixture::class.java, "toMax"))
            .isEqualTo(listOf("+1000000000-12-31T22:59:59.999999999Z", "+1000000000-12-31T23:59:59.999999999Z"))
    }

    /** An instant has no format argument, so the message has none to name. */
    @Test
    fun testUnparseableBoundIsReportedWithoutAFormat() {
        assertThat(executionFailure(Fixture::class.java, "unparseable"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("Unable to parse min string [2024-01-01]")
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun hourly(@InstantRangeSource(min = "2024-01-01T00:00:00Z", max = "2024-01-01T02:00:00Z") instant: Instant) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun descendingEveryHalfHour(
            @InstantRangeSource(
                min = "2024-01-01T00:00:00Z", max = "2024-01-01T01:00:00Z", increment = "PT30m", ascending = false
            )
            instant: Instant
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun toMax(@InstantRangeSource(min = "+1000000000-12-31T22:59:59.999999999Z", max = "MAX") instant: Instant) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun unparseable(@InstantRangeSource(min = "2024-01-01", max = "2024-01-02T00:00:00Z") instant: Instant) {
        }
    }
}
