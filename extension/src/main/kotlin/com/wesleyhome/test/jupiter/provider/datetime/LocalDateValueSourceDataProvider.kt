package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.toLocalDate
import java.time.LocalDate

internal class LocalDateValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<LocalDate, LocalDateSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDate?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDate(localDateSource.dateFormat) }
            .toList()
    }
}
