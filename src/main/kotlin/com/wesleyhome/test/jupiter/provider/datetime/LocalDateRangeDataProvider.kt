package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalDate
import java.time.LocalDate

class LocalDateRangeDataProvider : AbstractAnnotatedDateTimeRangeDataProvider<LocalDate, LocalDateRangeSource>() {
    override val formatPropertyName: String = "dateFormat"
    override fun toList(min: LocalDate, max: LocalDate, increment: String): List<LocalDate> {
        return (min..max step increment).toList()
    }

    override fun convert(value: String, format: String): LocalDate = value.toLocalDate(format)

}
