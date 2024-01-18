package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.params.provider.Arguments
import java.util.concurrent.atomic.AtomicLong

class ArgumentParameters(private val options: List<List<Any?>>) : Iterable<Arguments> {
    private val totalPermutations: Long = options.map { it.size }
        .map { it.toLong() }
        .reduce { acc, i -> acc * i }
    private val pointers: Array<Int> = Array(options.size) { 0 }
    private val current = AtomicLong(0)

    override fun iterator(): Iterator<Arguments> {
        return object : Iterator<Arguments> {
            override fun hasNext(): Boolean {
                return current.get() < totalPermutations
            }

            override fun next(): Arguments {
                return createArgument().also { increment() }
            }
        }
    }

    fun createArgument(): Arguments {
        val indexed = options
            .mapIndexed { index, list ->
                val paramIndex = pointers[index]
                list[paramIndex]
            }
        return Arguments.of(*indexed.toTypedArray())
    }

    private fun increment() {
        current.incrementAndGet()
        incrementIndex(pointers.size - 1)
    }

    private fun incrementIndex(index: Int) {
        if (index < 0) {
            return
        }
        val parameterIndex = pointers[index] + 1
        val length = options[index].size
        if (parameterIndex < length) {
            pointers[index] = parameterIndex
        } else {
            pointers[index] = 0
            incrementIndex(index - 1)
        }
    }

}
