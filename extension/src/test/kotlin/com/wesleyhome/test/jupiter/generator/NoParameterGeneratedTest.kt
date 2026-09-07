package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.testkit.invocationCounts
import org.junit.jupiter.api.Test

class NoParameterGeneratedTest {

    /**
     * A `@GeneratedParametersTest` with no parameters has an empty Cartesian product, which is a
     * product of one - it runs exactly once rather than failing the container.
     */
    @Test
    fun testTestWithoutParametersRunsExactlyOnce() {
        assertThat(invocationCounts(Fixture::class.java, "withoutParameters"))
            .isEqualTo(1L to 1L)
    }

    class Fixture {

        @GeneratedParametersTest
        fun withoutParameters() {
        }
    }
}
