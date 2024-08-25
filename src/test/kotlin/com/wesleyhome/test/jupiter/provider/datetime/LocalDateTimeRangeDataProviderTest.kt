package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalDateTime
import java.time.LocalDateTime

class LocalDateTimeRangeDataProviderTest :
    AnnotatedDateTimeRangeDataProviderTest<LocalDateTimeRangeDataProvider, LocalDateTime, LocalDateTimeRangeSource>() {

    override val defaultIncrementString: String = "PT1h"
    override val defaultDateFormatString: String = "yyyy-MM-dd HH:mm"

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @StringSource(["", "Unparseable", "2024-01-01 5:30", "2024-01-01 6:30", "2024-01-01 7:30", "2024-01-01 9:30", "01/01/2024 15:30", "2024-01-01"])
        min: String,
        @StringSource(["", "Unparseable", "2024-01-01 05:30", "2024-01-01 10:30", "01/01/2024 15:30", "2024-01-01"])
        max: String,
        @StringSource(["", "Unparseable", "PT2h", "PT6h"])
        increment: String?,
        @StringSource(["", "Unparseable", "yyyy-MM-dd hh:mm", "MM/dd/yyyy hh:mm", "yyyy-MM-dd"])
        dateFormat: String?,
        ascending: Boolean?
    ) {
        createAndAssertTestParameter(min, max, increment, ascending, dateFormat)
    }

    override fun convert(valueString: String, dateFormat: String): LocalDateTime = valueString.toLocalDateTime(dateFormat)

    override fun expectedList(min: LocalDateTime, max: LocalDateTime, increment: String): List<LocalDateTime> {
        return (min..max step increment).toList()
    }
}
