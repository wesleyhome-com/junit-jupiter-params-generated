package com.wesleyhome.test.jupiter.provider.datetime

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.wesleyhome.test.jupiter.GeneratedParametersClock
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.RandomInstantSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import java.time.Instant

/**
 * The README claims a rerun reproduces the previous sequence. With `useOffset = true` that was
 * false: the seed was fixed but the range it applied to was anchored to `Instant.now()`, so the
 * values slid with the wall clock and a failure found today need not reproduce tomorrow.
 */
class RandomInstantDeterminismTest {

    private val fixedAt = GeneratedParametersClock.FIXED_AT_PROPERTY

    @Test
    fun testOffsetBoundsAreReproducibleUnderAFixedClock() {
        val first = invocationNames(Fixture::class.java, "offset", fixedAt to "2024-01-01T00:00:00Z")
        val second = invocationNames(Fixture::class.java, "offset", fixedAt to "2024-01-01T00:00:00Z")
        assertThat(first).hasSize(5)
        assertThat(second).isEqualTo(first)
    }

    @Test
    fun testOffsetBoundsFollowTheClock() {
        val january = invocationNames(Fixture::class.java, "offset", fixedAt to "2024-01-01T00:00:00Z")
        val june = invocationNames(Fixture::class.java, "offset", fixedAt to "2024-06-01T00:00:00Z")
        assertThat(june).isNotEqualTo(january)
    }

    @Test
    fun testValuesSitWithinTheOffsetWindowAroundTheFixedInstant() {
        val origin = Instant.parse("2024-01-01T00:00:00Z")
        val values = invocationNames(Fixture::class.java, "offset", fixedAt to "2024-01-01T00:00:00Z")
            .map(Instant::parse)
        assertThat(values.all { it >= origin.minusSeconds(86_400) && it <= origin.plusSeconds(86_400) })
            .isTrue()
    }

    /** Absolute bounds never depended on the clock, so fixing it must not change them. */
    @Test
    fun testAbsoluteBoundsAreUnaffectedByTheClock() {
        val withClock = invocationNames(Fixture::class.java, "absolute", fixedAt to "2024-01-01T00:00:00Z")
        val withoutClock = invocationNames(Fixture::class.java, "absolute")
        assertThat(withClock).isEqualTo(withoutClock)
    }

    @Test
    fun testSeedChangesTheSequence() {
        val defaultSeed = invocationNames(Fixture::class.java, "absolute")
        val otherSeed = invocationNames(Fixture::class.java, "absoluteWithOtherSeed")
        assertThat(otherSeed).hasSize(defaultSeed.size)
        assertThat(otherSeed).isNotEqualTo(defaultSeed)
    }

    @Test
    fun testAMalformedFixedInstantIsReported() {
        assertThat(executionFailure(Fixture::class.java, "offset", fixedAt to "yesterday"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("$fixedAt must be an ISO-8601 instant, but was [yesterday]")
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun offset(
            @RandomInstantSource(min = "-P1D", max = "P1D", size = 5, useOffset = true) value: Instant
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun absolute(
            @RandomInstantSource(min = "2024-01-01T00:00:00Z", max = "2024-12-31T00:00:00Z", size = 5)
            value: Instant
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun absoluteWithOtherSeed(
            @RandomInstantSource(
                min = "2024-01-01T00:00:00Z",
                max = "2024-12-31T00:00:00Z",
                size = 5,
                seed = 99L
            ) value: Instant
        ) {
        }
    }
}
