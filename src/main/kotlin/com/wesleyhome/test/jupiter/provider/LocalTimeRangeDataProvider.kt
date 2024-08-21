package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import java.time.LocalTime
import kotlin.reflect.KClass

object LocalTimeRangeDataProvider : AbstractAnnotatedParameterDataProvider<LocalTime, LocalTimeRangeSource>() {

    override val annotation: KClass<LocalTimeRangeSource> = LocalTimeRangeSource::class

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
