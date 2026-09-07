package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import org.junit.jupiter.api.AfterAll
import java.util.concurrent.atomic.AtomicInteger

class NoParameterGeneratedTest {

    /**
     * A `@GeneratedParametersTest` with no parameters has an empty Cartesian product,
     * which is a product of one - it runs exactly once rather than failing the container.
     */
    @GeneratedParametersTest
    fun testWithoutParameters() {
        invocations.incrementAndGet()
    }

    companion object {
        private val invocations = AtomicInteger(0)

        @JvmStatic
        @AfterAll
        fun assertRanExactlyOnce() {
            assertThat(invocations.get()).isEqualTo(1)
        }
    }
}
