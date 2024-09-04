package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.formatter
import com.wesleyhome.test.jupiter.propertyValue
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.temporalAmount
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
        val errorList = mutableListOf<String>()
        val dateFormat =
            try {
                val test = dateFormatString ?: defaultDateFormatString
                test.formatter()
                test
            } catch (e: Exception) {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessageContaining("Invalid date format: [${dateFormatString!!}]")
                }
                return
            }


        try {
            convert(minString, dateFormat)
        } catch (e: Exception) {
            errorList += "Unable to parse min string [$minString] using format [$dateFormat]"
        }
        try {
            convert(maxString, dateFormat)
        } catch (e: Exception) {
            errorList += "Unable to parse max string [$maxString] using format [$dateFormat]"
        }
        val (min: T, max: T) = if (errorList.isEmpty()) {
            val min = convert(minString, dateFormat)
            val max = convert(maxString, dateFormat)
            if (min >= max) {
                errorList += "Min value [$minString] must be less than max value [$maxString]"
            }
            min to max
        } else {
            val annotation = createTrueProvidesForTestParameter().annotations.first()
            val min = annotation.propertyValue<String>("min")
            val max = annotation.propertyValue<String>("max")
            val format = annotation.propertyValue<String>(provider.formatPropertyName)
            convert(min, format) to convert(max, format)
        }
        val increment =
            try {
                val test = incrementString ?: defaultIncrementString
                test.temporalAmount()
                test
            } catch (e: DateTimeParseException) {
                errorList += "Unable to parse increment [$incrementString] into a duration or period"
                incrementString!!
            }
        val ascending = ascendingNullable ?: defaultAscending
        if (errorList.isEmpty()) {
            val expected = expectedList(min, max, increment)
                .let { if (ascending) it else it.reversed() }
            testCreateParameterOptionsData(testParameter, false) {
                it.isEqualTo(expected)
            }
        } else {
            testCreateParameterOptionsDataWithException(testParameter) {
                it.isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage(errorList.joinToString(", "))
            }
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter =
        testParameter("2024-01-01 12:00", "2024-02-01 12:00", dateFormat = "yyyy-MM-dd HH:mm")

    abstract fun convert(valueString: String, format: String): T

    abstract fun expectedList(min: T, max: T, increment: String): List<T>
}

fun <T> List<T>.plusIf(error: T, block: () -> Unit): List<T> {
    try {
        block()
        return this
    } catch (e: Exception) {
        return this + error
    }
}
