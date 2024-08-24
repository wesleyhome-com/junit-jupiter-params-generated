package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.RandomInstantSource
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class RandomInstanceSourceDataProviderTest :
    AnnotatedParameterDataProviderTest<RandomInstanceSourceDataProvider, Instant, RandomInstantSource>() {

    @GeneratedParametersTest
    fun testCreateParameterOptionsData(
        @StringSource(["", "2024-06-01T12:00:00Z"])
        minInstant: String,
        @StringSource(["", "2024-06-02T12:00:00Z"])
        maxInstant: String,
        @StringSource(["", "-P1D"])
        minOffset: String,
        @StringSource(["", "-P1D"])
        maxOffset: String,
        @StringSource(["MINUTES", "HOURS", "SECONDS"])
        truncateTo: String,
        @IntRangeSource(min = -10, max = 10)
        size: Int,
    ) {
        val testParameter = createAnnotatedTestParameter(
            size,
            minInstant,
            maxInstant,
            minOffset,
            maxOffset,
            truncateTo
        )
        when {
            minInstant.isBlank() && minOffset.isBlank() -> {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage("Either [minInstant] or [minOffset] must be provided")
                }
            }

            minInstant.isNotBlank() && maxInstant.isBlank() -> {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage("[maxInstant] must be provided when [minInstant] is provided")
                }
            }

            minOffset.isNotBlank() && maxOffset.isBlank() -> {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage("[maxOffset] must be provided when [minOffset] is provided")
                }
            }
            size <= 0 -> {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage("[size] must be greater than 0")
                }
            }
            else -> {
                val now = ZonedDateTime.now()
                val minValue = if (minInstant.isNotBlank()) minInstant.toInstant() else minOffset.toInstant(now, ChronoUnit.valueOf(truncateTo))
                val maxValue = if (maxInstant.isNotBlank()) maxInstant.toInstant() else maxOffset.toInstant(now, ChronoUnit.valueOf(truncateTo))
                testCreateParameterOptionsData(testParameter, false) {
                    it.allSatisfy { value ->
                        value.isBetween(minValue, maxValue)
                    }
                }
            }
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        val size: Int = 5
        return createAnnotatedTestParameter(
            size,
            "2024-06-01T12:00:00Z",
            "2024-06-02T12:00:00Z",
            "-P1D",
            "-P1D",
            "SECONDS"
        )
    }
}

fun Instant?.isBetween(min: Instant, max: Instant): Boolean {
    return this != null && (this == min || this == max || (isAfter(min) && isBefore(max)))
}
