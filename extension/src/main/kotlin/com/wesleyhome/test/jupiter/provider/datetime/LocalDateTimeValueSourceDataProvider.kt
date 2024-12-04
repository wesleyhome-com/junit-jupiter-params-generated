package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.toLocalDateTime
import java.time.LocalDateTime

internal class LocalDateTimeValueSourceDataProvider :
    AbstractAnnotatedParameterDataProvider<LocalDateTime, LocalDateTimeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDateTime?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDateTime(localDateSource.dateTimeFormat) }
            .toList()
    }
}
