package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import java.time.LocalDate
import kotlin.reflect.KClass

object LocalDateRangeDataProvider : AbstractAnnotatedParameterDataProvider<LocalDate, LocalDateRangeSource>() {

    override val annotation: KClass<LocalDateRangeSource> = LocalDateRangeSource::class

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
