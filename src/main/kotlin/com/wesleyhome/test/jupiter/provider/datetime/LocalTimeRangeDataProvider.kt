package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalTime
import java.time.LocalTime

class LocalTimeRangeDataProvider : AbstractAnnotatedDateTimeRangeDataProvider<LocalTime, LocalTimeRangeSource>() {

    override val formatPropertyName: String = "timeFormat"

    override fun convert(value: String, format: String): LocalTime = value.toLocalTime(format)

    override fun toList(min: LocalTime, max: LocalTime, increment: String): List<LocalTime> =
        (min .. max step increment).toList()

}
