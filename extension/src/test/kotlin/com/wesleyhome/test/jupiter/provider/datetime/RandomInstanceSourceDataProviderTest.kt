package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.datetime.RandomInstantSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.validation.datetime.TruncateChronoUnit
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.temporalAmount
import java.time.Instant
import java.time.ZonedDateTime

internal class RandomInstanceSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<RandomInstanceSourceDataProvider, Instant, RandomInstantSource>() {

    companion object {
        private val offsetList = listOf("-P1D", "-P2D", "P1D", "P2D")
        private val instantList =
            listOf("2024-06-01T12:00:00Z", "2024-06-02T12:00:00Z", "2024-06-03T12:00:00Z", "2024-06-03T12:00:00Z")
    }

    @GeneratedParametersTest
    fun testCreateParameterOptionsData(
        @StringSource(["", "Unparseable", "2024-06-01T12:00:00Z", "2024-06-02T12:00:00Z", "-P1D", "-P2D"])
        minString: String,
        @StringSource(["", "Unparseable", "2024-06-03T12:00:00Z", "2024-06-03T12:00:00Z", "P1D", "P2D"])
        maxString: String,
        useOffset: Boolean,
        truncateTo: TruncateChronoUnit,
        @IntRangeSource(min = -10, max = 10)
        size: Int,
    ) {
        val testParameter = createAnnotatedTestParameter(
            minString,
            maxString,
            useOffset,
            size,
            truncateTo
        )
        val expectedErrors = mutableListOf<String>()
        if (minString.isBlank()) {
            expectedErrors.add("Invalid [min] value: [$minString]")
        }
        if (maxString.isBlank()) {
            expectedErrors.add("Invalid [max] value: [$maxString]")
        }
        if (expectedErrors.isNotEmpty()) {
            testCreateParameterOptionsDataWithException(testParameter) {
                it.isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage(expectedErrors.joinToString("\n"))
            }
        } else {
            if (useOffset) {
                if (minString !in offsetList) {
                    expectedErrors.add("Invalid [min] offset value: [$minString]")
                }
                if (maxString !in offsetList) {
                    expectedErrors.add("Invalid [max] offset value: [$maxString]")
                }
            } else {
                if (minString !in instantList) {
                    expectedErrors.add("Invalid [min] value: [$minString]")
                }
                if (maxString !in instantList) {
                    expectedErrors.add("Invalid [max] value: [$maxString]")
                }
            }
            if (size < 1) {
                expectedErrors.add("[size] must be greater than 0")
            }
            if (expectedErrors.isNotEmpty()) {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage(expectedErrors.joinToString("\n"))
                }
            } else {
                val now = ZonedDateTime.now()
                val minValue = if (useOffset) {
                    now.toInstant().plus(minString.temporalAmount())
                } else {
                    minString.toInstant()
                }
                val maxValue = if (useOffset) {
                    now.toInstant().plus(maxString.temporalAmount())
                } else {
                    maxString.toInstant()
                }
                testCreateParameterOptionsData(testParameter, false) {
                    it.allSatisfy() { value ->
                        value.isBetween(minValue, maxValue)
                    }
                }
            }
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        return createAnnotatedTestParameter(
            "2024-06-01T12:00:00Z",
            "2024-06-02T12:00:00Z",
            false,
            10,
            TruncateChronoUnit.SECONDS
        )
    }
}

internal fun Instant?.isBetween(min: Instant, max: Instant): Boolean {
    return this != null && (this == min || this == max || (isAfter(min) && isBefore(max)))
}
