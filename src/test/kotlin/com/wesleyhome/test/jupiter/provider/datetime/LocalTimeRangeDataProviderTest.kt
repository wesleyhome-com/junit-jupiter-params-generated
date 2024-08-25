package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalTime
import java.time.LocalTime

class LocalTimeRangeDataProviderTest :
    AnnotatedDateTimeRangeDataProviderTest<LocalTimeRangeDataProvider, LocalTime, LocalTimeRangeSource>() {

    override val defaultIncrementString: String = "PT1h"
    override val defaultDateFormatString: String = "HH:mm"

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @StringSource(["", "Unparseable", "5:30", "6:30", "7:30", "9:30", "01/01/2024 15:30", "2024-01-01"])
        min: String,
        @StringSource(["", "Unparseable", "15:30", "16:30", "17:30", "19:30", "01/01/2024 4:30", "2024-01-01"])
        max: String,
        @StringSource(["", "Unparseable", "PT2h", "PT6h"])
        increment: String?,
        @StringSource(["", "Unparseable", "yyyy-MM-dd hh:mm", "MM/dd/yyyy hh:mm", "yyyy-MM-dd"])
        dateFormat: String?,
        ascending: Boolean?
    ) {
        createAndAssertTestParameter(min, max, increment, ascending, dateFormat)
    }

    override fun convert(valueString: String, format: String): LocalTime =
        valueString.toLocalTime(format)

    override fun expectedList(min: LocalTime, max: LocalTime, increment: String): List<LocalTime> {
        return (min..max step increment).toList()
    }
}
