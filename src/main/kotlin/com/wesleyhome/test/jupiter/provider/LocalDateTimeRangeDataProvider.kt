package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import java.time.LocalDateTime
import kotlin.reflect.KClass

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
