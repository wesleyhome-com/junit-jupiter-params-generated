package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isIn
import assertk.assertions.isInstanceOf
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

class ExtremeDatesTest {

    @Test
    fun testLocalDateSourceAcceptsMinAndMax() {
        assertThat(invocationNames(Fixture::class.java, "minAndMaxDates"))
            .isEqualTo(listOf("-999999999-01-01", "+999999999-12-31"))
    }

    /** The default still resolves an out-of-range day to the month's last, as `yyyy-MM-dd` always did. */
    @Test
    fun testLocalDateSourceParsesOrdinaryDatesAsBefore() {
        assertThat(invocationNames(Fixture::class.java, "ordinaryDates"))
            .isEqualTo(listOf("2024-01-15", "2024-02-29", "2024-02-29", "0000-01-01"))
    }

    @Test
    fun testLocalDateSourceUsesACustomFormatAsGiven() {
        assertThat(invocationNames(Fixture::class.java, "customFormat")).isEqualTo(listOf("2024-01-02"))
    }

    @Test
    fun testCustomYearOfEraFormatStillRejectsNegativeYears() {
        assertThat(executionFailure(Fixture::class.java, "customFormatNegativeYear"))
            .isInstanceOf(DateTimeParseException::class)
    }

    @Test
    fun testLocalDateRangeStartingAtMin() {
        assertThat(invocationNames(Fixture::class.java, "dateRangeAtMin"))
            .isEqualTo(listOf("-999999999-01-01", "-999999999-01-02", "-999999999-01-03"))
    }

    /** Stepping past the last value would overflow the year, so the range has to stop before it. */
    @Test
    fun testLocalDateRangeEndingAtMax() {
        assertThat(invocationNames(Fixture::class.java, "dateRangeAtMax"))
            .isEqualTo(listOf("+999999999-12-30", "+999999999-12-31"))
    }

    @Test
    fun testLocalDateRangeWhoseStepOverflowsBeforeMax() {
        assertThat(invocationNames(Fixture::class.java, "dateRangeStepPastMax")).isEqualTo(listOf("+999999999-12-30"))
    }

    @Test
    fun testDescendingLocalDateRangeEndingAtMax() {
        assertThat(invocationNames(Fixture::class.java, "descendingDateRangeAtMax"))
            .isEqualTo(listOf("+999999999-12-31", "+999999999-12-30"))
    }

    @Test
    fun testLocalDateTimeSourceAcceptsMinAndMax() {
        assertThat(invocationNames(Fixture::class.java, "minAndMaxDateTimes"))
            .isEqualTo(listOf("-999999999-01-01T00:00", "+999999999-12-31T23:59"))
    }

    @Test
    fun testLocalDateTimeSourceUsesACustomFormatAsGiven() {
        assertThat(invocationNames(Fixture::class.java, "customDateTimeFormat")).isEqualTo(listOf("2024-01-02T03:04"))
    }

    @Test
    fun testLocalDateTimeRangeEndingAtMax() {
        assertThat(invocationNames(Fixture::class.java, "dateTimeRangeAtMax"))
            .isEqualTo(listOf("+999999999-12-31T22:00", "+999999999-12-31T23:00"))
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun minAndMaxDates(@LocalDateSource(["-999999999-01-01", "+999999999-12-31"]) date: LocalDate) {
            assertThat(date).isIn(LocalDate.MIN, LocalDate.MAX)
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun ordinaryDates(@LocalDateSource(["2024-01-15", "2024-02-29", "2024-02-30", "0000-01-01"]) date: LocalDate) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun customFormat(@LocalDateSource(["01/02/2024"], dateFormat = "MM/dd/yyyy") date: LocalDate) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun customFormatNegativeYear(
            @LocalDateSource(["-999999999/01/01"], dateFormat = "yyyy/MM/dd") date: LocalDate
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun dateRangeAtMin(@LocalDateRangeSource(min = "-999999999-01-01", max = "-999999999-01-03") date: LocalDate) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun dateRangeAtMax(@LocalDateRangeSource(min = "+999999999-12-30", max = "+999999999-12-31") date: LocalDate) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun dateRangeStepPastMax(
            @LocalDateRangeSource(min = "+999999999-12-30", max = "+999999999-12-31", increment = "P2d") date: LocalDate
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun descendingDateRangeAtMax(
            @LocalDateRangeSource(min = "+999999999-12-30", max = "+999999999-12-31", ascending = false) date: LocalDate
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun minAndMaxDateTimes(
            @LocalDateTimeSource(["-999999999-01-01 00:00", "+999999999-12-31 23:59"]) dateTime: LocalDateTime
        ) {
            assertThat(dateTime).isIn(LocalDateTime.MIN, LocalDateTime.MAX.truncatedTo(ChronoUnit.MINUTES))
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun customDateTimeFormat(
            @LocalDateTimeSource(["01/02/2024 03:04"], dateTimeFormat = "MM/dd/yyyy HH:mm") dateTime: LocalDateTime
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun dateTimeRangeAtMax(
            @LocalDateTimeRangeSource(min = "+999999999-12-31 22:00", max = "+999999999-12-31 23:00")
            dateTime: LocalDateTime
        ) {
        }
    }
}
