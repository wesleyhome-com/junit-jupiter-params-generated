package com.wesleyhome.test.jupiter.generator

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.wesleyhome.test.jupiter.MAX_PERMUTATIONS_PROPERTY
import com.wesleyhome.test.jupiter.TooManyPermutationsException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationCounts
import org.junit.jupiter.api.Test

class CardinalityGuardTest {

    @Test
    fun testProductAboveTheCeilingIsRefused() {
        assertThat(executionFailure(Fixture::class.java, "product", MAX_PERMUTATIONS_PROPERTY to "50"))
            .isInstanceOf(TooManyPermutationsException::class)
            .hasMessage(
                "@GeneratedParametersTest would generate 100 invocations, above the maximum of 50: " +
                    "a=10 x b=10. Reduce the generated values, or raise $MAX_PERMUTATIONS_PROPERTY."
            )
    }

    @Test
    fun testProductAtTheCeilingIsAllowed() {
        assertThat(invocationCounts(Fixture::class.java, "product", MAX_PERMUTATIONS_PROPERTY to "100"))
            .isEqualTo(100L to 100L)
    }

    /**
     * The largest test in this repository generates 12,099 invocations, so the shipped default has
     * to clear that comfortably or it would fail suites that run today.
     */
    @Test
    fun testDefaultCeilingAllowsAnOrdinarySuite() {
        assertThat(invocationCounts(Fixture::class.java, "product")).isEqualTo(100L to 100L)
    }

    @Test
    fun testAProductTooLargeToCountIsRefused() {
        assertThat(executionFailure(Fixture::class.java, "uncountable"))
            .isInstanceOf(TooManyPermutationsException::class)
            .hasMessage(
                "@GeneratedParametersTest would generate more invocations than can be counted, " +
                    "above the maximum of 1000000: " +
                    (1..10).joinToString(" x ") { "v$it=1000000" } +
                    ". Reduce the generated values, or raise $MAX_PERMUTATIONS_PROPERTY."
            )
    }

    @Test
    fun testAMalformedCeilingIsReported() {
        val failure = executionFailure(Fixture::class.java, "product", MAX_PERMUTATIONS_PROPERTY to "lots")
        assertThat(failure.message).isNotNull().all {
            contains(MAX_PERMUTATIONS_PROPERTY)
            contains("[lots]")
        }
    }

    class Fixture {

        @GeneratedParametersTest
        fun product(
            @IntRangeSource(min = 1, max = 10) a: Int,
            @IntRangeSource(min = 1, max = 10) b: Int
        ) {
        }

        @GeneratedParametersTest
        @Suppress("LongParameterList")
        fun uncountable(
            @IntRangeSource(min = 1, max = 1_000_000) v1: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v2: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v3: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v4: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v5: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v6: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v7: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v8: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v9: Int,
            @IntRangeSource(min = 1, max = 1_000_000) v10: Int
        ) {
        }
    }
}
