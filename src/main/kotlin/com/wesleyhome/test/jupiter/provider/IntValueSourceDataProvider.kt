package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.IntSource
import kotlin.reflect.KClass

class IntValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Int, IntSource>() {

    override val annotation: KClass<IntSource> = IntSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Int?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
