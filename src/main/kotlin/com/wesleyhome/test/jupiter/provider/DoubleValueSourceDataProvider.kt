package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DoubleSource
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

class DoubleValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Double, DoubleSource>() {

    override val annotation: KClass<DoubleSource> = DoubleSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Double?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}

class DoubleProgression(
    min: Double,
    max: Double,
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
