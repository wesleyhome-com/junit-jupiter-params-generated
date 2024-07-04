package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import java.time.LocalTime

object LocalTimeRangeDataProvider : AbstractParameterDataProvider<LocalTime>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalTime?> {
        val s = findAnnotation(testParameter)!!
        val timeFormat = s.timeFormat
        val min = s.min.toLocalTime(timeFormat)
        val max = s.max.toLocalTime(timeFormat)
        val range = (min..max step s.increment).toList()
        return if(s.ascending) {
            range
        } else {
            range.reversed()
        }
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalTimeRangeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalTimeRangeSource
            }
        }

}
