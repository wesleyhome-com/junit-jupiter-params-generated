package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.validation.datetime.DateTimeRangeValidator
import com.wesleyhome.test.jupiter.propertyValue
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter

internal abstract class AbstractAnnotatedDateTimeRangeDataProvider<T : Comparable<T>, A : Annotation> :
    AbstractAnnotatedParameterDataProvider<T, A>() {

    open val formatPropertyName: String = ""

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val annotation = findAnnotation(testParameter)!!
        val format = getFormatString(annotation)
        val minString = annotation.propertyValue<String>("min")
        val maxString = annotation.propertyValue<String>("max")
        val increment = annotation.propertyValue<String>("increment")
        DateTimeRangeValidator.validate(minString, maxString, increment, format, this::convert).apply {
            if (isNotEmpty()) {
                throw IllegalArgumentException(joinToString(", "))
            }
        }
        val min = convert(minString, format)
        val max = convert(maxString, format)
        val ascending = annotation.propertyValue<Boolean>("ascending")
        val range = toList(min, max, increment)
        return if (ascending) {
            range
        } else {
            range.reversed()
        }
    }

    open fun getFormatString(annotation: A): String = annotation.propertyValue<String>(formatPropertyName)

    abstract fun toList(min: T, max: T, increment: String): List<T>

    abstract fun convert(value: String, format: String): T
}


