package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.RandomInstantSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.temporalAmount
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.reflect.KClass

class RandomInstanceSourceDataProvider : AbstractAnnotatedParameterDataProvider<Instant, RandomInstantSource>() {
    override val annotation: KClass<RandomInstantSource> = RandomInstantSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val annotation = findAnnotation(testParameter)!!
        val minInstant: String = annotation.minInstant
        val maxInstant: String = annotation.maxInstant
        val minOffset: String = annotation.minOffset
        val maxOffset: String = annotation.maxOffset
        val truncateTo: String = annotation.truncateTo
        val size: Int = annotation.size

        if(minInstant.isBlank() && minOffset.isBlank()) {
            throw IllegalArgumentException("Either minInstant or startPeriodOffset must be provided")
        }
        if(minInstant.isNotBlank() && maxInstant.isBlank()) {
            throw IllegalArgumentException("[maxInstant] must be provided when minInstant is provided")
        }
        if(minOffset.isNotBlank() && maxOffset.isBlank()) {
            throw IllegalArgumentException("[endPeriodOffset] must be provided when startPeriodOffset is provided")
        }
        val truncationUnit = if(minOffset.isNotBlank()) {
            ChronoUnit.valueOf(truncateTo)
        } else {
            ChronoUnit.MILLIS
        }
        if(size < 1) {
            throw IllegalArgumentException("Size must be greater than 0")
        }
        val now = ZonedDateTime.now().truncatedTo(truncationUnit).toInstant()
        val min = if(minInstant.isBlank()) {
            now.plus(minOffset.temporalAmount())
        } else {
            Instant.parse(minInstant)
        }.toEpochMilli()
        val max = if(maxInstant.isBlank()) {
            now.plus(maxOffset.temporalAmount())
        } else {
            Instant.parse(maxInstant)
        }.toEpochMilli()
        val range = (min .. max)
        return (1..size)
            .map { Random.nextLong(range) }
            .map { Instant.ofEpochMilli(it) }
            .toList()
    }
}
