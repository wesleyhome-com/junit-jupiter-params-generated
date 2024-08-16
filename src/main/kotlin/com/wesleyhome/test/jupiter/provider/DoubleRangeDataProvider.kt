package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DoubleRangeSource
import kotlin.reflect.KClass

class DoubleRangeDataProvider : AbstractAnnotatedParameterDataProvider<Double, DoubleRangeSource>() {

    override val annotation: KClass<DoubleRangeSource> = DoubleRangeSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Double?> {
        val s = findAnnotation(testParameter)!!
        if (s.increment <= 0) {
            throw IllegalArgumentException("increment must be greater than 0")
        }
        if (s.min >= s.max) {
            throw IllegalArgumentException("min must be less than or equal to max")
        }
        val range = (s.min..s.max step s.increment).toList()
        return if (s.ascending) {
            range
        } else {
            range.reversed()
        }
    }
}
