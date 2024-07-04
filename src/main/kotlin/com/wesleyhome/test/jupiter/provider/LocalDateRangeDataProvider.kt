package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import java.time.LocalDate

object LocalDateRangeDataProvider : AbstractParameterDataProvider<LocalDate>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

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

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalDateRangeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalDateRangeSource
            }
        }
}
