package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.InstantRangeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.temporalAmount
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.reflect.KClass

class InstantRangeSourceDataProvider : AbstractAnnotatedParameterDataProvider<Instant, InstantRangeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val annotation = findAnnotation(testParameter)!!
        val minInstant = annotation.minInstant
        val maxInstant = annotation.maxInstant
        val minOffset = annotation.minOffset
        val maxOffset = annotation.maxOffset
        val ascending = annotation.ascending
        val increment = if(ascending) annotation.increment else "-${annotation.increment}"
        val truncateTo = annotation.truncateTo
        if(minInstant.isBlank() && minOffset.isBlank()) {
            throw IllegalArgumentException("Either [minInstant] or [minOffset] must be provided")
        }
        if(minInstant.isNotBlank() && maxInstant.isBlank()) {
            throw IllegalArgumentException("[maxInstant] must be provided when [minInstant] is provided")
        }
        if(minOffset.isNotBlank() && maxOffset.isBlank()) {
            throw IllegalArgumentException("[maxOffset] must be provided when [minOffset] is provided")
        }
        val truncationUnit = if(minOffset.isNotBlank()) {
            ChronoUnit.valueOf(truncateTo)
        } else {
            ChronoUnit.MILLIS
        }
        val now = ZonedDateTime.now().truncatedTo(truncationUnit).toInstant()
        val min = if(minInstant.isBlank()) {
            now.plus(minOffset.temporalAmount())
        } else {
            Instant.parse(minInstant)
        }
        val max = if(maxInstant.isBlank()) {
            now.plus(maxOffset.temporalAmount())
        } else {
            Instant.parse(maxInstant)
        }
        return (min .. max step increment).toList()
    }
}
