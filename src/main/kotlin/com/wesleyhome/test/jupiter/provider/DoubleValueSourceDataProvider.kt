package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.DoubleSource
import java.util.concurrent.atomic.AtomicReference

object DoubleValueSourceDataProvider : AbstractParameterDataProvider<Double>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<Double?> {
        return findAnnotation(testParameter)!!.values.toList()
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is DoubleSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as DoubleSource
            }
        }
}

object DoubleRangeDataProvider : AbstractParameterDataProvider<Double>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

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

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is DoubleRangeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as DoubleRangeSource
            }
        }
}

class DoubleProgression(
    private val min: Double,
    private val max: Double,
    private val step: Double
) : Iterable<Double> {

    private val range: ClosedFloatingPointRange<Double> = min..max
    private val current = AtomicReference(min)

    override fun iterator(): Iterator<Double> {
        return object : Iterator<Double> {
            override fun hasNext(): Boolean = range.contains(current.get())

            override fun next(): Double {
                return current.getAndUpdate { it + step }
            }
        }
    }
}

infix fun ClosedFloatingPointRange<Double>.step(step: Double): DoubleProgression {
    return DoubleProgression(this.start, this.endInclusive, step)
}
