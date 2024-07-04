package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.RandomInstantSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Duration
import java.time.Instant
import java.time.Period
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAmount
import kotlin.random.Random
import kotlin.random.nextLong
import kotlin.reflect.KClass

object RandomInstanceSourceDataProvider : AbstractAnnotatedParameterDataProvider<Instant, RandomInstantSource>() {
    override val annotation: KClass<RandomInstantSource> = RandomInstantSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val annotation = findAnnotation(testParameter)!!
        val minInstant: String = annotation.minInstant
        val maxInstant: String = annotation.maxInstant
        val startPeriodOffset: String = annotation.startPeriodOffset
        val endPeriodOffset: String = annotation.endPeriodOffset
        val size: Int = annotation.size

        if(minInstant.isBlank() && startPeriodOffset.isBlank()) {
            throw IllegalArgumentException("Either minInstant or startPeriodOffset must be provided")
        }
        if(maxInstant.isBlank() && endPeriodOffset.isBlank()) {
            throw IllegalArgumentException("Either maxInstant or endPeriodOffset must be provided")
        }
        if(size < 1) {
            throw IllegalArgumentException("Size must be greater than 0")
        }
        val now = Instant.now()
        val min = if(minInstant.isBlank()) {
            val temporalAmount: TemporalAmount = try {
                Period.parse(startPeriodOffset)
            } catch (e: DateTimeParseException) {
                Duration.parse(startPeriodOffset)
            }
            now.plus(temporalAmount)
        } else {
            Instant.parse(minInstant)
        }.toEpochMilli()
        val max = if(maxInstant.isBlank()) {
            val temporalAmount: TemporalAmount = try {
                Period.parse(endPeriodOffset)
            } catch (e: DateTimeParseException) {
                Duration.parse(endPeriodOffset)
            }
            now.plus(temporalAmount)
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
