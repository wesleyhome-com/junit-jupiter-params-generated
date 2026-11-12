package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class ValueClassesTest {

    @Test
    fun testMangledNameIsReadBackAsDeclared() {
        val method = Fixture::class.java.declaredMethods.single { it.name.startsWith("takes-money-") }
        assertThat(method.kotlinName()).isEqualTo("takes-money")
    }

    /** Left alone, so JUnit reports the mismatch exactly as it does for any other wrong type. */
    @Test
    fun testValueThatIsNotAValueClassPassesThroughUnchanged() {
        assertThat("text".adaptedTo(Int::class.javaPrimitiveType!!)).isEqualTo("text")
    }

    class Fixture {
        fun `takes-money`(amount: Money): Long = amount.cents
    }
}
