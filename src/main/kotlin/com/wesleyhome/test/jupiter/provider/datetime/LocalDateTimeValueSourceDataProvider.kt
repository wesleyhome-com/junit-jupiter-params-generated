package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalDateTimeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.toLocalDateTime
import java.time.LocalDateTime

class LocalDateTimeValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<LocalDateTime, LocalDateTimeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDateTime?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDateTime(localDateSource.dateTimeFormat) }
            .toList()
    }
}
