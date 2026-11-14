package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.validation.datetime.DateTimeRangeValidator
import com.wesleyhome.test.jupiter.propertyValue
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter

internal abstract class AbstractAnnotatedDateTimeRangeDataProvider<T : Comparable<T>, A : Annotation> :
    AbstractAnnotatedParameterDataProvider<T, A>() {

    /** Null for a type that is parsed without a format, such as an ISO-8601 instant. */
    open val formatPropertyName: String? = null

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val annotation = findAnnotation(testParameter)!!
        val format = formatPropertyName?.let { annotation.propertyValue<String>(it) }
        val parse = { value: String -> convert(value, format.orEmpty()) }
        val minString = annotation.propertyValue<String>("min")
        val maxString = annotation.propertyValue<String>("max")
        val increment = annotation.propertyValue<String>("increment")
        DateTimeRangeValidator.validate(minString, maxString, increment, format, parse).apply {
            if (isNotEmpty()) {
                throw IllegalArgumentException(joinToString(", "))
            }
        }
        val min = parse(minString)
        val max = parse(maxString)
        val ascending = annotation.propertyValue<Boolean>("ascending")
        val range = toList(min, max, increment)
        return if (ascending) {
            range
        } else {
            range.reversed()
        }
    }

    abstract fun toList(min: T, max: T, increment: String): List<T>

    /** [format] is empty when [formatPropertyName] is null. */
    abstract fun convert(value: String, format: String): T
}
