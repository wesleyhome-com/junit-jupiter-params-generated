package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.formatter
import com.wesleyhome.test.jupiter.provider.temporalAmount
import java.time.format.DateTimeParseException

abstract class AnnotatedDateTimeRangeDataProviderTest
<P : AbstractAnnotatedDateTimeRangeDataProvider<T, A>, T : Comparable<T>, A : Annotation>
    : AnnotatedParameterDataProviderTest<P, T, A>() {
    open val defaultAscending: Boolean = true
    abstract val defaultIncrementString: String
    abstract val defaultDateFormatString: String

    fun createAndAssertTestParameter(
        min: String,
        max: String,
        increment: String?,
        ascending: Boolean?,
        dateFormat: String?
    ) {
        val testParameter = testParameter(min, max, increment, ascending, dateFormat)
        assertParameters(testParameter, min, max, increment, ascending, dateFormat)
    }

    private fun testParameter(
        min: String,
        max: String,
        increment: String? = null,
        ascending: Boolean? = null,
        dateFormat: String? = null
    ) = createAnnotatedTestParameter(
        min,
        max,
        increment ?: defaultIncrementString,
        ascending ?: defaultAscending,
        dateFormat ?: defaultDateFormatString
    )

    protected fun assertParameters(
        testParameter: TestParameter,
        minString: String,
        maxString: String,
        incrementString: String?,
        ascendingNullable: Boolean?,
        dateFormatString: String?
    ) {
        val dateFormat =
            try {
                val test = dateFormatString ?: defaultDateFormatString
                test.formatter()
                test
            } catch (e: IllegalArgumentException) {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessageStartingWith("Unknown pattern letter: ${dateFormatString!!.first()}")
                }
                return
            }

        try {
            convert(minString, dateFormat)
        } catch (e: DateTimeParseException) {
            testCreateParameterOptionsDataWithException(testParameter) {
                it.isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Unable to parse $minString using format $dateFormat")
            }
            return
        }
        try {
            convert(maxString, dateFormat)
        } catch (e: DateTimeParseException) {
            testCreateParameterOptionsDataWithException(testParameter) {
                it.isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Unable to parse $maxString using format $dateFormat")
            }
            return
        }
        val min = convert(minString, dateFormat)
        val max = convert(maxString, dateFormat)
        if (min >= max) {
            testCreateParameterOptionsDataWithException(testParameter) {
                it.isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("$maxString is not after $minString")
            }
            return
        }

        val increment =
            try {
                val test = incrementString ?: defaultIncrementString
                test.temporalAmount()
                test
            } catch (e: DateTimeParseException) {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessageContaining("Unable to parse increment $incrementString into a duration or period")
                }
                return
            }
        val ascending = ascendingNullable ?: defaultAscending
        val expected = expectedList(min, max, increment)
            .let { if (ascending) it else it.reversed() }
        testCreateParameterOptionsData(testParameter, false) {
            it.isEqualTo(expected)
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter =
        testParameter("2024-01-01 12:00", "2024-02-01 12:00", dateFormat = "yyyy-MM-dd hh:mm")

    abstract fun convert(valueString: String, format: String): T

    abstract fun expectedList(min: T, max: T, increment: String): List<T>
}
