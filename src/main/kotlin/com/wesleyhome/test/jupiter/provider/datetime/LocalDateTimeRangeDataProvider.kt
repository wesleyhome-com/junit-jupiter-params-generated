package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalDateTime
import java.time.LocalDateTime

class LocalDateTimeRangeDataProvider : AbstractAnnotatedParameterDataProvider<LocalDateTime, LocalDateTimeRangeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDateTime?> {
        val s = findAnnotation(testParameter)!!
        val dateFormat = s.dateTimeFormat
        val range = (s.min.toLocalDateTime(dateFormat)..s.max.toLocalDateTime(dateFormat) step s.increment).toList()
        return if (s.ascending) {
            range
        } else {
            range.reversed()
        }
    }
}
