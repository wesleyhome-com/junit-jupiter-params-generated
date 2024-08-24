package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.RandomInstantSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.temporalAmount
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlin.random.nextLong

class RandomInstanceSourceDataProvider : AbstractAnnotatedParameterDataProvider<Instant, RandomInstantSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val annotation = findAnnotation(testParameter)!!
        val minInstant: String = annotation.minInstant
        val maxInstant: String = annotation.maxInstant
        val minOffset: String = annotation.minOffset
        val maxOffset: String = annotation.maxOffset
        val truncateTo: String = annotation.truncateTo
        val size: Int = annotation.size

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
        if(size < 1) {
            throw IllegalArgumentException("[size] must be greater than 0")
        }
        val now = ZonedDateTime.now().truncatedTo(truncationUnit).toInstant()
        var min = if(minInstant.isBlank()) {
            now.plus(minOffset.temporalAmount())
        } else {
            Instant.parse(minInstant)
        }
        val max = if(maxInstant.isBlank()) {
            now.plus(maxOffset.temporalAmount())
        } else {
            Instant.parse(maxInstant)
        }
        if(min.isAfter(max)) {
            min = max.plus(minOffset.temporalAmount())
        }
        val range = (min.toEpochMilli() .. max.toEpochMilli())
        return (1..size)
            .map { Random.nextLong(range) }
            .map { Instant.ofEpochMilli(it) }
            .toList()
    }
}
