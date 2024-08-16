package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LongSource
import kotlin.reflect.KClass

class LongValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Long, LongSource>() {

    override val annotation: KClass<LongSource> = LongSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
