package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.step
import com.wesleyhome.test.jupiter.toLocalDateTime
import java.time.LocalDateTime

internal class LocalDateTimeRangeDataProviderTest :
    AnnotatedDateTimeRangeDataProviderTest<LocalDateTimeRangeDataProvider, LocalDateTime, LocalDateTimeRangeSource>() {

    override val defaultIncrementString: String = "PT1h"
    override val defaultDateFormatString: String = "yyyy-MM-dd HH:mm"

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @StringSource(["", "Unparseable", "2024-01-01 05:30", "2024-01-01 06:30", "2024-01-01 07:30", "2024-01-01 09:30", "01/01/2024 15:30", "2024-01-01"])
        min: String,
        @StringSource(["", "Unparseable", "2024-01-01 05:30", "2024-01-01 10:30", "01/01/2024 15:30", "2024-01-01"])
        max: String,
        @StringSource(["", "Unparseable", "PT2h", "PT6h"])
        increment: String?,
        @StringSource(["", "Unparseable", "yyyy-MM-dd HH:mm", "MM/dd/yyyy HH:mm", "yyyy-MM-dd"])
        dateFormat: String?,
        ascending: Boolean?
    ) {
        createAndAssertTestParameter(min, max, increment, ascending, dateFormat)
    }

    override fun convert(valueString: String, format: String): LocalDateTime =
        valueString.toLocalDateTime(format)

    override fun expectedList(min: LocalDateTime, max: LocalDateTime, increment: String): List<LocalDateTime> {
        return (min..max step increment).toList()
    }
}
