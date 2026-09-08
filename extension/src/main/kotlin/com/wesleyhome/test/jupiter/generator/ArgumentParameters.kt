package com.wesleyhome.test.jupiter.generator

import java.util.stream.LongStream
import java.util.stream.Stream

internal class ArgumentParameters(private val options: List<List<Any?>>) : Iterable<Array<Any?>> {

    val totalPermutations: Long = run {
        var total = 1L
        for (option in options) {
            if (option.isEmpty()) return@run 0L
            total = try {
                Math.multiplyExact(total, option.size.toLong())
            } catch (_: ArithmeticException) {
                return@run Long.MAX_VALUE
            }
        }
        total
    }

    /** Right-most parameter advances every invocation, left-most only after a full sweep of the rest. */
    private val strides: LongArray = LongArray(options.size).also { strides ->
        var stride = 1L
        for (i in options.indices.reversed()) {
            strides[i] = stride
            stride = try {
                Math.multiplyExact(stride, options[i].size.toLong())
            } catch (_: ArithmeticException) {
                Long.MAX_VALUE
            }
        }
    }

    fun valuesAt(permutation: Long): Array<Any?> {
        val values = arrayOfNulls<Any?>(options.size)
        for (i in options.indices) {
            val option = options[i]
            values[i] = option[((permutation / strides[i]) % option.size).toInt()]
        }
        return values
    }

    fun stream(): Stream<Array<Any?>> = LongStream.range(0, totalPermutations).mapToObj(::valuesAt)

    override fun iterator(): Iterator<Array<Any?>> = object : Iterator<Array<Any?>> {
        private var next = 0L
        override fun hasNext(): Boolean = next < totalPermutations
        override fun next(): Array<Any?> = valuesAt(next++)
    }
}
