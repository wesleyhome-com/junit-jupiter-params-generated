package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.InstantRangeSource
import com.wesleyhome.test.jupiter.step
import java.time.Instant

internal class InstantRangeSourceDataProvider : AbstractAnnotatedDateTimeRangeDataProvider<Instant, InstantRangeSource>() {
    override fun toList(
        min: Instant,
        max: Instant,
        increment: String
    ): List<Instant> =
        (min..max step increment).toList()

    override fun convert(value: String, format: String): Instant = Instant.parse(value)
}
