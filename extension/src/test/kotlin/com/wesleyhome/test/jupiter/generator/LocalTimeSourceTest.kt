package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeSource
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import java.time.LocalTime

class LocalTimeSourceTest {

    @Test
    fun testLocalTimeSource() {
        assertThat(invocationNames(Fixture::class.java, "values")).isEqualTo(listOf("12:30", "09:15"))
    }

    @Test
    fun testLocalTimeSourceWithACustomFormat() {
        assertThat(invocationNames(Fixture::class.java, "customFormat")).isEqualTo(listOf("13:05:09"))
    }

    @Test
    fun testMinAndMaxKeywordsForLocalTime() {
        assertThat(invocationNames(Fixture::class.java, "keywords")).isEqualTo(listOf("00:00", "23:59:59.999999999"))
    }

    @Test
    fun testLocalTimeRange() {
        assertThat(invocationNames(Fixture::class.java, "range")).isEqualTo(listOf("09:00", "09:30", "10:00"))
    }

    /** The step after 23:00 wraps to 00:00, which is back inside the range. */
    @Test
    fun testRangeStopsAtMidnight() {
        assertThat(invocationNames(Fixture::class.java, "wholeDay")).isEqualTo(HOURS)
    }

    @Test
    fun testDescendingRangeStopsAtMidnight() {
        assertThat(invocationNames(Fixture::class.java, "wholeDayDescending")).isEqualTo(HOURS.reversed())
    }

    @Test
    fun testRangeBetweenKeywords() {
        assertThat(invocationNames(Fixture::class.java, "minToMax"))
            .isEqualTo(listOf("00:00", "06:00", "12:00", "18:00"))
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun values(@LocalTimeSource(["12:30", "09:15"]) time: LocalTime) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun customFormat(@LocalTimeSource(["1:05:09 PM"], timeFormat = "h:mm:ss a") time: LocalTime) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun keywords(@LocalTimeSource(["MIN", "MAX"]) time: LocalTime) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun range(@LocalTimeRangeSource(min = "09:00", max = "10:00", increment = "PT30m") time: LocalTime) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun wholeDay(@LocalTimeRangeSource(min = "00:00", max = "23:30") time: LocalTime) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun wholeDayDescending(@LocalTimeRangeSource(min = "00:00", max = "23:30", ascending = false) time: LocalTime) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun minToMax(@LocalTimeRangeSource(min = "MIN", max = "MAX", increment = "PT6h") time: LocalTime) {
        }
    }

    private companion object {
        val HOURS = (0..23).map { "%02d:00".format(it) }
    }
}
