package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateSource
import java.time.LocalDate
import kotlin.reflect.KClass

object LocalDateValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<LocalDate, LocalDateSource>() {

    override val annotation: KClass<LocalDateSource> = LocalDateSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDate?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDate(localDateSource.dateFormat) }
            .toList()
    }
}
