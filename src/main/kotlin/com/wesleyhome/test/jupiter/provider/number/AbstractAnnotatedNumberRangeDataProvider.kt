package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.propertyValue
import com.wesleyhome.test.jupiter.provider.step

abstract class AbstractAnnotatedNumberRangeDataProvider<T : Number, A : Annotation> :
    AbstractAnnotatedParameterDataProvider<T, A>() {

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val s = findAnnotation(testParameter)!!
        val min = convert(s.propertyValue("min"))
        val max = convert(s.propertyValue("max"))
        val increment = convert(s.propertyValue("increment"))
        val ascending = s.propertyValue<Boolean>("ascending")
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

    abstract fun convert(value: Number): T

}
