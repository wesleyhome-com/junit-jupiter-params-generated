package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.formatter
import com.wesleyhome.test.jupiter.provider.propertyValue
import com.wesleyhome.test.jupiter.provider.temporalAmount
import java.time.format.DateTimeParseException

abstract class AbstractAnnotatedDateTimeRangeDataProvider<T: Comparable<T>, A: Annotation> : AbstractAnnotatedParameterDataProvider<T, A>() {

    open val formatPropertyName = "dateFormat"

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val annotation = findAnnotation(testParameter)!!
        val format = annotation.propertyValue<String>(formatPropertyName)
        format.formatter()
        val minString = annotation.propertyValue<String>("min")
        val min = try {
            convert(minString, format)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Unable to parse $minString using format $format", e)
        }
        val maxString = annotation.propertyValue<String>("max")
        val max = try {
            convert(maxString, format)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Unable to parse $maxString using format $format", e)
        }
        if(min >= max) {
            throw IllegalArgumentException("$maxString is not after $minString")
        }
        val increment = annotation.propertyValue<String>("increment")
        assertIncrementFormat(increment)
        val ascending = annotation.propertyValue<Boolean>("ascending")
        val range = toList(min, max, increment)
        return if (ascending) {
            range
        } else {
            range.reversed()
        }
    }

    abstract fun toList(min: T, max: T, increment: String): List<T>

    abstract fun convert(value: String, format: String): T

    private fun assertIncrementFormat(increment: String) {
        try {
            increment.temporalAmount()
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("Unable to parse increment $increment into a duration or period")
        }
    }
}


