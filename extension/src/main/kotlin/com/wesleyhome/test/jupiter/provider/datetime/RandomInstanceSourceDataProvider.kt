package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.RandomInstantSource
import com.wesleyhome.test.jupiter.annotations.validation.datetime.RandomDateTimeValidator
import com.wesleyhome.test.jupiter.temporalAmount
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

internal class RandomInstanceSourceDataProvider : AbstractAnnotatedRandomDateTimeDataProvider<Instant, RandomInstantSource>() {

    override fun getFormatString(annotation: RandomInstantSource): String {
        return ""
    }

    override fun now(truncationUnit: ChronoUnit): Instant = Instant.now().truncatedTo(truncationUnit)

    override fun longRange(range: ClosedRange<Instant>): LongRange {
        return LongRange(range.start.toEpochMilli(), range.endInclusive.toEpochMilli())
    }

    override fun addOffset(
        value: Instant,
        offset: TemporalAmount
    ): Instant = value.plus(offset)

    override fun convert(value: String, format: String): Instant = Instant.parse(value)

    override fun convert(longValue: Long): Instant =
        Instant.ofEpochMilli(longValue)

    override fun validate(
        minString: String,
        maxString: String,
        size: Int,
        useOffset: Boolean
    ): List<String> {
        return RandomDateTimeValidator.validate(minString, maxString, size, useOffset, { it ->
            it.toInstant()
        }) { it ->
            it.temporalAmount()
        }
    }

}

internal fun String.toInstant(): Instant = Instant.parse(this)
