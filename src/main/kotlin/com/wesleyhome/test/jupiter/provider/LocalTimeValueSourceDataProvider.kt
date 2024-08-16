package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalTimeSource
import java.time.LocalTime
import kotlin.reflect.KClass

class LocalTimeValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<LocalTime, LocalTimeSource>() {

    override val annotation: KClass<LocalTimeSource> = LocalTimeSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalTime?> {
        val localTimeSource = findAnnotation(testParameter)!!
        return localTimeSource.values
            .map { it.toLocalTime(localTimeSource.timeFormat) }
            .toList()

    }
}
