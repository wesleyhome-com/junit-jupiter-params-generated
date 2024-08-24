package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.number.step
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalDate
import java.time.LocalDate

class LocalDateRangeDataProvider : AbstractAnnotatedParameterDataProvider<LocalDate, LocalDateRangeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDate?> {
        val s = findAnnotation(testParameter)!!
        val dateFormat = s.dateFormat
        val range = (s.min.toLocalDate(dateFormat)..s.max.toLocalDate(dateFormat) step s.increment).toList()
        return if (s.ascending) {
            range
        } else {
            range.reversed()
        }
    }
}
