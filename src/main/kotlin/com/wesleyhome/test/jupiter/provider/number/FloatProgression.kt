package com.wesleyhome.test.jupiter.provider.number

import java.util.concurrent.atomic.AtomicReference

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
