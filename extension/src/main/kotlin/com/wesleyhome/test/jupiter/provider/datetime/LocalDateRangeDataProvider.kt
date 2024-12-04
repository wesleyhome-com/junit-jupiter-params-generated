package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.step
import com.wesleyhome.test.jupiter.toLocalDate
import java.time.LocalDate

internal class LocalDateRangeDataProvider : AbstractAnnotatedDateTimeRangeDataProvider<LocalDate, LocalDateRangeSource>() {
    override val formatPropertyName: String = "dateFormat"
    override fun toList(min: LocalDate, max: LocalDate, increment: String): List<LocalDate> {
        return (min..max step increment).toList()
    }

    override fun convert(value: String, format: String): LocalDate = value.toLocalDate(format)

}
