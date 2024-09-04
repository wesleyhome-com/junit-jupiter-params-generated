package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.toLocalTime
import java.time.LocalTime

class LocalTimeValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<LocalTime, LocalTimeSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalTime?> {
        val localTimeSource = findAnnotation(testParameter)!!
        return localTimeSource.values
            .map { it.toLocalTime(localTimeSource.timeFormat) }
            .toList()

    }
}
