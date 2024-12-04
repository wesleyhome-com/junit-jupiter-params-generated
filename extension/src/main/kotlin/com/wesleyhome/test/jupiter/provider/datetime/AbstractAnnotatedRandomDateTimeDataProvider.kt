package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.validation.datetime.TruncateChronoUnit
import com.wesleyhome.test.jupiter.propertyValue
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.temporalAmount
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount
import kotlin.random.Random
import kotlin.random.nextLong

internal abstract class AbstractAnnotatedRandomDateTimeDataProvider<T : Comparable<T>, A : Annotation>
    : AbstractAnnotatedParameterDataProvider<T, A>() {

    open val formatPropertyName: String = ""

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val annotation = findAnnotation(testParameter)!!
        val minString = annotation.propertyValue<String>("min")
        val maxString = annotation.propertyValue<String>("max")
        val size = annotation.propertyValue<Int>("size")
        val useOffset = annotation.propertyValue<Boolean>("useOffset")
        val errors = validate(minString, maxString, size, useOffset)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString("\n"))
        }
        val truncateTo = annotation.propertyValue<TruncateChronoUnit>("truncateTo")
        val format = getFormatString(annotation)
        val truncationUnit = if (useOffset) truncateTo.chronoUnit else ChronoUnit.MILLIS
        val now: T = now(truncationUnit)
        var min: T = if (useOffset) {
            addOffset(now, minString.temporalAmount())
        } else {
            convert(minString, format)
        }
        var max = if (useOffset) {
            addOffset(now, maxString.temporalAmount())
        } else {
            convert(maxString, format)
        }
        if (min > max) {
            val temp = min
            min = max
            max = temp
        }
        val range = longRange(min..max)
        return (1..size)
            .map { Random.nextLong(range) }
            .map { it -> convert(it) }
            .toList()
    }

    abstract fun validate(minString: String, maxString: String, size: Int, useOffset: Boolean): List<String>

    open fun getFormatString(annotation: A): String = annotation.propertyValue<String>(formatPropertyName)

    abstract fun longRange(range: ClosedRange<T>): LongRange

    abstract fun now(truncationUnit: ChronoUnit): T

    abstract fun addOffset(value: T, offset: TemporalAmount): T

    abstract fun convert(value: String, format: String): T

    abstract fun convert(longValue: Long): T

}
