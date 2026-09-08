package com.wesleyhome.test.jupiter.annotations.processor

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test

class RangeAnnotationValidatorTest {

    @Test
    fun testNumberRangeWithMinGreaterThanMax() {
        assertThat(validate("IntRangeSource", "min" to 10, "max" to 1, "increment" to 1))
            .containsExactly("Min value [10] cannot be greater than max value [1]")
    }

    @Test
    fun testNumberRangeWithZeroIncrement() {
        assertThat(validate("LongRangeSource", "min" to 1L, "max" to 10L, "increment" to 0L))
            .containsExactly("Increment must be greater than zero")
    }

    @Test
    fun testValidNumberRange() {
        assertThat(validate("DoubleRangeSource", "min" to 0.0, "max" to 1.0, "increment" to 0.1)).isEmpty()
    }

    @Test
    fun testNumberRangeIgnoresOtherArguments() {
        assertThat(validate("FloatRangeSource", "min" to 1.0f, "max" to 2.0f, "ascending" to false)).isEmpty()
    }

    @Test
    fun testDateRangeWithMinGreaterThanMax() {
        assertThat(validate("LocalDateRangeSource", "min" to "2024-06-01", "max" to "2024-01-01"))
            .containsExactly("Min value [2024-06-01] cannot be greater than max value [2024-01-01]")
    }

    @Test
    fun testDateRangeWithUnparseableFormat() {
        assertThat(
            validate(
                "LocalDateRangeSource",
                "min" to "2024-01-01", "max" to "2024-02-01", "dateFormat" to "yyyy-MM-dd'"
            )
        ).containsExactly("Invalid date format: [yyyy-MM-dd']")
    }

    @Test
    fun testDateRangeWithUnparseableBound() {
        assertThat(validate("LocalDateRangeSource", "min" to "not-a-date", "max" to "2024-02-01"))
            .containsExactly("Unable to parse min string [not-a-date] using format [yyyy-MM-dd]")
    }

    @Test
    fun testDateRangeWithUnparseableIncrement() {
        assertThat(
            validate(
                "LocalDateRangeSource",
                "min" to "2024-01-01", "max" to "2024-02-01", "increment" to "every other Tuesday"
            )
        ).containsExactly("Unable to parse increment [every other Tuesday] into a duration or period")
    }

    /** Each annotation names its format argument differently; the wrong name would silently default. */
    @Test
    fun testDateTimeRangeUsesItsOwnFormatArgument() {
        assertThat(
            validate(
                "LocalDateTimeRangeSource",
                "min" to "01/06/2024 10:00", "max" to "01/01/2024 10:00",
                "dateTimeFormat" to "dd/MM/yyyy HH:mm"
            )
        ).containsExactly("Min value [01/06/2024 10:00] cannot be greater than max value [01/01/2024 10:00]")
    }

    @Test
    fun testTimeRangeUsesItsOwnFormatArgument() {
        assertThat(validate("LocalTimeRangeSource", "min" to "18:00", "max" to "09:00", "timeFormat" to "HH:mm"))
            .containsExactly("Min value [18:00] cannot be greater than max value [09:00]")
    }

    @Test
    fun testInstantRangeHasNoFormatArgument() {
        assertThat(validate("InstantRangeSource", "min" to "2024-06-01T00:00:00Z", "max" to "2024-01-01T00:00:00Z"))
            .containsExactly(
                "Min value [2024-06-01T00:00:00Z] cannot be greater than max value [2024-01-01T00:00:00Z]"
            )
    }

    @Test
    fun testValidRangesOfEveryDateTimeKind() {
        assertThat(validate("LocalDateRangeSource", "min" to "2024-01-01", "max" to "2024-02-01")).isEmpty()
        assertThat(
            validate("LocalDateTimeRangeSource", "min" to "2024-01-01 00:00", "max" to "2024-01-02 00:00")
        ).isEmpty()
        assertThat(validate("LocalTimeRangeSource", "min" to "09:00", "max" to "17:00")).isEmpty()
        assertThat(
            validate("InstantRangeSource", "min" to "2024-01-01T00:00:00Z", "max" to "2024-02-01T00:00:00Z")
        ).isEmpty()
    }

    /** Defaults are what a caller who omits the argument gets, so they have to validate as valid. */
    @Test
    fun testDefaultIncrementAndFormatAreUsedWhenArgumentsAreAbsent() {
        assertThat(validate("LocalDateRangeSource", "min" to "2024-01-01", "max" to "2024-02-01")).isEmpty()
        assertThat(validate("LocalTimeRangeSource", "min" to "09:00", "max" to "17:00")).isEmpty()
    }

    @Test
    fun testAnnotationWithNoRulesIsAccepted() {
        assertThat(validate("StringSource", "values" to arrayOf("a"))).isEmpty()
    }

    @Test
    fun testUnreadableBoundIsReportedRatherThanThrown() {
        assertThat(validate("IntRangeSource", "max" to 1)).containsExactly("Unable to read [min]")
        assertThat(validate("LocalDateRangeSource", "min" to "2024-01-01")).containsExactly("Unable to read [max]")
    }

    private fun validate(annotationName: String, vararg arguments: Pair<String, Any?>) =
        RangeAnnotationValidator.validate(annotationName, arguments.toMap())
}
