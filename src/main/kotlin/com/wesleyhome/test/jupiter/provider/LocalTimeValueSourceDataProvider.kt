package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalTimeSource
import java.time.LocalTime

object LocalTimeValueSourceDataProvider : AbstractParameterDataProvider<LocalTime>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalTime?> {
        val localTimeSource = findAnnotation(testParameter)!!
        return localTimeSource.values
            .map { it.toLocalTime(localTimeSource.timeFormat) }
            .toList()

    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalTimeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalTimeSource
            }
        }

}
