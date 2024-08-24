package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateTimeSource
import java.time.LocalDateTime
import kotlin.reflect.KClass

class LocalDateTimeValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<LocalDateTime, LocalDateTimeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDateTime?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDateTime(localDateSource.dateTimeFormat) }
            .toList()
    }
}
