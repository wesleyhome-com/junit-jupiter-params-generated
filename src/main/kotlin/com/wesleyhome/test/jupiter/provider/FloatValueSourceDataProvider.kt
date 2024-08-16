package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.FloatSource
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

class FloatValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Float, FloatSource>() {

    override val annotation: KClass<FloatSource> = FloatSource::class

    override fun createParameterOptionsData(testParameter: TestParameter): List<Float?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}

class FloatProgression(
    min: Float,
    max: Float,
    private val step: Float
) : Iterable<Float> {

    private val range: ClosedFloatingPointRange<Float> = min..max
    private val current = AtomicReference(min)

    override fun iterator(): Iterator<Float> {
        return object : Iterator<Float> {
            override fun hasNext(): Boolean = range.contains(current.get())

            override fun next(): Float {
                return current.getAndUpdate { it + step }
            }
        }
    }
}

infix fun ClosedFloatingPointRange<Float>.step(step: Float): FloatProgression {
    return FloatProgression(this.start, this.endInclusive, step)
}
