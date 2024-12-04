package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.validation.number.NumberRangeValidator
import com.wesleyhome.test.jupiter.propertyValue
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.step

internal abstract class AbstractAnnotatedNumberRangeDataProvider<T : Number, A : Annotation> :
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
        val errors = NumberRangeValidator.validate(min, max, increment)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString(", "))
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
