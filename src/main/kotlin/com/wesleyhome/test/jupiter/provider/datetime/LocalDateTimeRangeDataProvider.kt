package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalDateTime
import java.time.LocalDateTime

class LocalDateTimeRangeDataProvider :
    AbstractAnnotatedDateTimeRangeDataProvider<LocalDateTime, LocalDateTimeRangeSource>() {
    override val formatPropertyName: String = "dateTimeFormat"

    override fun toList(min: LocalDateTime, max: LocalDateTime, increment: String): List<LocalDateTime> =
        (min..max step increment).toList()

    override fun convert(value: String, format: String): LocalDateTime = value.toLocalDateTime(format)

}
