package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.validation.number.NumberRangeValidator
import com.wesleyhome.test.jupiter.propertyValue
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.math.BigDecimal
import java.math.RoundingMode

internal abstract class AbstractAnnotatedNumberRangeDataProvider<T : Number, A : Annotation> :
    AbstractAnnotatedParameterDataProvider<T, A>() {

    final override fun createParameterOptionsData(testParameter: TestParameter): List<T?> {
        val s = findAnnotation(testParameter)!!
        val min = convert(s.propertyValue("min"))
        val max = convert(s.propertyValue("max"))
        val increment = convert(s.propertyValue("increment"))
        val ascending = s.propertyValue<Boolean>("ascending")
        val errors = NumberRangeValidator.validate(min, max, increment)
        if (errors.isNotEmpty()) {
            throw IllegalArgumentException(errors.joinToString(", "))
        }
        val range = range(min.exactly(), max.exactly(), increment.exactly()).map { convert(it) }
        return if (ascending) {
            range
        } else {
            range.reversed()
        }
    }

    /**
     * Produces each value as `min + index * increment` rather than by repeatedly adding the
     * increment to a running total. Accumulating in [Double] drifts - a step of `0.1` reaches
     * `0.30000000000000004` and then never lands on `max` - and it silently truncates [Long]
     * values above 2^53. Stepping by index in [BigDecimal] does neither.
     */
    private fun range(min: BigDecimal, max: BigDecimal, increment: BigDecimal): List<BigDecimal> {
        val steps = (max - min).divide(increment, 0, RoundingMode.FLOOR).toLong()
        return (0..steps).map { index -> min + increment * BigDecimal.valueOf(index) }
    }

    abstract fun convert(value: Number): T

}

/**
 * The decimal this number reads as, rather than the binary [Double] it would widen to: `0.1f`
 * becomes `0.1`, and a [Long] beyond 2^53 keeps every digit.
 */
private fun Number.exactly(): BigDecimal = BigDecimal(this.toString())
