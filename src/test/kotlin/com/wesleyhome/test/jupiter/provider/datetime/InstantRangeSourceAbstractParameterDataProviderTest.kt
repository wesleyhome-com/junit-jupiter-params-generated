package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.InstantRangeSource
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.number.step
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.temporalAmount
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class InstantRangeSourceAbstractParameterDataProviderTest :
    AnnotatedParameterDataProviderTest<InstantRangeSourceDataProvider, Instant, InstantRangeSource>() {

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
        @StringSource(["PT1h", "PT2h"])
        increment: String,
        ascending: Boolean
    ) {
        val testParameter = createAnnotatedTestParameter(
            minInstant,
            maxInstant,
            minOffset,
            maxOffset,
            truncateTo,
            increment,
            ascending
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

            else -> {
                val now = ZonedDateTime.now()
                val minValue = if (minInstant.isNotBlank()) minInstant.toInstant() else minOffset.toInstant(now, ChronoUnit.valueOf(truncateTo))
                val maxValue = if (maxInstant.isNotBlank()) maxInstant.toInstant() else maxOffset.toInstant(now, ChronoUnit.valueOf(truncateTo))
                val expected = (minValue..maxValue step if(ascending) increment else "-$increment").toList()
                testCreateParameterOptionsData(testParameter, false) {
                    it.containsExactlyElementsOf(expected)
                }
            }
        }
    }

    override fun createTrueProvidesForTestParameter(): TestParameter = createAnnotatedTestParameter(
        "2024-06-01T12:00:00Z",
        "2024-06-02T12:00:00Z",
        "-P1D",
        "-P1D",
        "SECONDS",
        "PT1h",
        true
    )

}

fun String.toInstant(now: ZonedDateTime? = null, truncateTo: ChronoUnit? = null): Instant = try {
    Instant.parse(this)
} catch (e: Exception) {
    val zonedDateTime = now ?: ZonedDateTime.now()
    val chronoUnit = truncateTo ?: ChronoUnit.HOURS
    zonedDateTime.truncatedTo(chronoUnit).plus(this.temporalAmount()).toInstant()
}
