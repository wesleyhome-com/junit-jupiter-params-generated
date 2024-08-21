package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.InstantRangeSource
import com.wesleyhome.test.jupiter.annotations.ParametersSource
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.temporalAmount
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.params.ParameterizedTest
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class InstantRangeSourceDataProviderTest {

    @ParameterizedTest
    @ParametersSource
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
        val annotation: InstantRangeSource = InstantRangeSource::class.constructors.first().call(
            minInstant,
            maxInstant,
            minOffset,
            maxOffset,
            truncateTo,
            increment,
            ascending
        )
        val testParameter = TestParameter(
            name = "testCreateParameterOptionsData",
            type = this::class,
            annotations = listOf(annotation)
        )
        when {
            minInstant.isBlank() && minOffset.isBlank() -> {
                assertThatThrownBy { InstantRangeSourceDataProvider.createParameterOptionsData(testParameter) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("Either [minInstant] or [minOffset] must be provided")

            }

            minInstant.isNotBlank() && maxInstant.isBlank() -> {
                assertThatThrownBy { InstantRangeSourceDataProvider.createParameterOptionsData(testParameter) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("[maxInstant] must be provided when [minInstant] is provided")
            }

            minOffset.isNotBlank() && maxOffset.isBlank() -> {
                assertThatThrownBy { InstantRangeSourceDataProvider.createParameterOptionsData(testParameter) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("[maxOffset] must be provided when [minOffset] is provided")
            }

            else -> {
                val now = ZonedDateTime.now()
                val optionsData = InstantRangeSourceDataProvider.createParameterOptionsData(testParameter)
                val minValue = if (minInstant.isNotBlank()) minInstant.toInstant() else minOffset.toInstant(now, ChronoUnit.valueOf(truncateTo))
                val maxValue = if (maxInstant.isNotBlank()) maxInstant.toInstant() else maxOffset.toInstant(now, ChronoUnit.valueOf(truncateTo))
                val expected = (minValue..maxValue step if(ascending) increment else "-$increment").toList()
                assertThat(optionsData).containsExactlyElementsOf(expected)
            }
        }
    }

    private fun String.toInstant(now: ZonedDateTime? = null, truncateTo: ChronoUnit? = null): Instant = try {
        Instant.parse(this)
    } catch (e: Exception) {
        val zonedDateTime = now ?: ZonedDateTime.now()
        val chronoUnit = truncateTo ?: ChronoUnit.HOURS
        zonedDateTime.truncatedTo(chronoUnit).plus(this.temporalAmount()).toInstant()
    }
}
