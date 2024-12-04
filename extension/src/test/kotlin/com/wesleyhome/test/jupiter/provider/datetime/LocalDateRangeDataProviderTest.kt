package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.step
import com.wesleyhome.test.jupiter.toLocalDate
import java.time.LocalDate

internal class LocalDateRangeDataProviderTest :
    AnnotatedDateTimeRangeDataProviderTest<LocalDateRangeDataProvider, LocalDate, LocalDateRangeSource>() {

    override val defaultIncrementString: String = "P1d"
    override val defaultDateFormatString: String = "yyyy-MM-dd"

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @StringSource(["", "Unparseable", "2024-01-01", "2024-02-01", "01/01/2024", "2024-01-01 15:25"])
        min: String,
        @StringSource(["", "Unparseable", "2024-02-01", "2024-04-01", "2024-08-01", "01/02/2024", "2024-02-01 15:25"])
        max: String,
        @StringSource(["", "Unparseable", "P2d", "P5d"])
        increment: String?,
        @StringSource(["", "Unparseable", "yyyy-MM-dd", "MM/dd/yyyy", "yyyy-MM-dd hh:mm"])
        dateFormat: String?,
        ascending: Boolean?
    ) {
        createAndAssertTestParameter(min, max, increment, ascending, dateFormat)
    }

    override fun convert(valueString: String, format: String): LocalDate = valueString.toLocalDate(format)

    override fun expectedList(min: LocalDate, max: LocalDate, increment: String): List<LocalDate> {
        return (min..max step increment).toList()
    }
}
