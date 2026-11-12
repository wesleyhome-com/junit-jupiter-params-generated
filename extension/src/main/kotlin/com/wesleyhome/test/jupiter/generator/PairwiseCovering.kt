package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidStrategyException

/**
 * Chooses combinations so that every pair of parameters appears with every pair of their values.
 *
 * The pairs to cover come from the combinations a filter actually accepts, not from the value lists,
 * so a pair no accepted combination contains is never treated as missing. Building a covering array
 * first and filtering it afterwards would instead drop whichever rows happened to carry those pairs,
 * and the guarantee would quietly stop holding.
 *
 * Selection is greedy over combinations in generation order and ties go to the lower index, so the
 * chosen rows are identical on every run and every machine.
 *
 * This walks the whole product rather than constructing rows directly, so it reduces what *runs* and
 * not what is generated; the invocation ceiling still applies to the full product.
 */
internal object PairwiseCovering {

    /** Two parameter indices and two value indices are packed into a Long, 16 bits each. */
    private const val MAX_OPTIONS = 1 shl 16

    fun rowsFor(arguments: ArgumentParameters, accepts: (Array<Any?>) -> Boolean): List<Array<Any?>> {
        if (arguments.parameterCount < 2) {
            return (0 until arguments.totalPermutations)
                .map { arguments.valuesAt(it) }
                .filter(accepts)
        }
        for (parameter in 0 until arguments.parameterCount) {
            if (arguments.optionCount(parameter) >= MAX_OPTIONS) {
                throw InvalidStrategyException(
                    "PAIRWISE supports at most $MAX_OPTIONS values per parameter, but one generates " +
                        "${arguments.optionCount(parameter)}. Pairwise reduces invocations when there are " +
                        "many parameters, not when there are many values."
                )
            }
        }

        val indices = IntArray(arguments.parameterCount)
        val accepted = ArrayList<Long>()
        val uncovered = HashSet<Long>()
        for (permutation in 0 until arguments.totalPermutations) {
            if (!accepts(arguments.valuesAt(permutation))) continue
            accepted.add(permutation)
            arguments.indicesInto(permutation, indices)
            forEachPair(indices) { uncovered.add(it) }
        }

        val chosen = mutableListOf<Long>()
        while (uncovered.isNotEmpty()) {
            var best = -1L
            var bestCount = 0
            for (permutation in accepted) {
                arguments.indicesInto(permutation, indices)
                var count = 0
                forEachPair(indices) { if (it in uncovered) count++ }
                if (count > bestCount) {
                    bestCount = count
                    best = permutation
                }
            }
            if (best < 0) break
            arguments.indicesInto(best, indices)
            forEachPair(indices) { uncovered.remove(it) }
            chosen.add(best)
        }
        // Selection order is greedy and arbitrary to read; generation order is what a report expects.
        return chosen.sorted().map { arguments.valuesAt(it) }
    }

    private inline fun forEachPair(indices: IntArray, action: (Long) -> Unit) {
        for (a in indices.indices) {
            for (b in a + 1 until indices.size) {
                action(
                    (a.toLong() shl 48) or
                        (indices[a].toLong() shl 32) or
                        (b.toLong() shl 16) or
                        indices[b].toLong()
                )
            }
        }
    }
}
