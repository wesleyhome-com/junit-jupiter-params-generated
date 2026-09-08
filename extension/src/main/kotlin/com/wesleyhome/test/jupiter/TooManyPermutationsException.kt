package com.wesleyhome.test.jupiter

/** JUnit Platform configuration parameter bounding the invocations one test method may generate. */
const val MAX_PERMUTATIONS_PROPERTY: String = "com.wesleyhome.test.jupiter.max.permutations"

/**
 * High enough that a test written to be read by a human does not reach it, low enough that a
 * mistyped range fails with a message instead of exhausting the heap.
 */
const val DEFAULT_MAX_PERMUTATIONS: Long = 1_000_000L

class TooManyPermutationsException internal constructor(
    parameterCounts: List<Pair<String, Int>>,
    total: Long,
    maximum: Long
) : RuntimeException(
    buildString {
        append("@GeneratedParametersTest would generate ")
        append(if (total == Long.MAX_VALUE) "more invocations than can be counted" else "$total invocations")
        append(", above the maximum of ").append(maximum).append(": ")
        append(parameterCounts.joinToString(" x ") { (name, count) -> "$name=$count" })
        append(". Reduce the generated values, or raise ").append(MAX_PERMUTATIONS_PROPERTY).append('.')
    }
)
