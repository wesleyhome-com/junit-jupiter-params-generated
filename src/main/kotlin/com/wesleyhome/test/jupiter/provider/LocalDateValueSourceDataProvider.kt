package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateSource
import java.time.LocalDate

object LocalDateValueSourceDataProvider : AbstractParameterDataProvider<LocalDate>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDate?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDate(localDateSource.dateFormat) }
            .toList()
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalDateSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalDateSource
            }
        }
}
