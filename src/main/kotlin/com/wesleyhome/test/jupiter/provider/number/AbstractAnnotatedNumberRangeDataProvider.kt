package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.step
import kotlin.reflect.full.memberProperties

abstract class AbstractAnnotatedNumberRangeDataProvider<T : Number, A : Annotation> :
    AbstractAnnotatedParameterDataProvider<T, A>() {

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val s = findAnnotation(testParameter)!!
        val min = convert(min(s))
        val max = convert(max(s))
        val increment = convert(increment(s))
        val ascending = ascending(s)
        val incrementDouble = increment.toDouble()
        val minDouble = min.toDouble()
        val maxDouble = max.toDouble()
        if (incrementDouble <= 0) {
            throw IllegalArgumentException("increment must be greater than 0")
        }
        if (minDouble >= maxDouble) {
            throw IllegalArgumentException("min must be less than max")
        }
        val range = (minDouble..maxDouble step incrementDouble).map { convert(it) }.toList()
        return if (ascending) {
            range
        } else {
            range.reversed()
        }
    }

    fun increment(a: A): T = callProperty("increment", a)

    fun min(a: A): T = callProperty("min", a)

    fun max(annotation: A): T = callProperty("max", annotation)

    fun ascending(annotation: A): Boolean = callProperty("ascending", annotation)

    abstract fun convert(value: Number): T

    private fun <V> callProperty(propertyName: String, a: A): V {
        return annotation.memberProperties
            .first { it.name == propertyName }
            .getter.call(a) as V
    }

}
