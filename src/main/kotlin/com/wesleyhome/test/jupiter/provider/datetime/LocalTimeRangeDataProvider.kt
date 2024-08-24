package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.number.step
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalTime
import java.time.LocalTime

class LocalTimeRangeDataProvider : AbstractAnnotatedParameterDataProvider<LocalTime, LocalTimeRangeSource>() {

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
}
