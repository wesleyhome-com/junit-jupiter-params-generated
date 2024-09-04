package com.wesleyhome.test.jupiter.provider.number

import java.util.concurrent.atomic.AtomicReference

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
