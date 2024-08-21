package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LongRangeSource
import kotlin.reflect.KClass

object LongRangeDataProvider : AbstractAnnotatedParameterDataProvider<Long, LongRangeSource>() {

    override val annotation: KClass<LongRangeSource> = LongRangeSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> {
        val s = findAnnotation(testParameter)!!
        if (s.increment <= 0) {
            throw IllegalArgumentException("increment must be greater than 0")
        }
        if (s.min >= s.max) {
            throw IllegalArgumentException("min must be less than or equal to max")
        }
        val range = if (s.ascending) {
            s.min..s.max step s.increment
        } else {
            s.max downTo s.min step s.increment
        }
        return range.toList()
    }
}
