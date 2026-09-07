package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo

class DisplayNameTest {

    @Test
    fun testDefaultDisplayName() {
        assertThat(invocationNames(Fixture::class.java, "defaultName"))
            .isEqualTo(listOf("[1] value=1", "[2] value=2"))
    }

    @Test
    fun testCustomDisplayName() {
        assertThat(invocationNames(Fixture::class.java, "customName"))
            .isEqualTo(listOf("value 1 of the run", "value 2 of the run"))
    }

    @Test
    fun testResolvedParametersAreNotNamed() {
        assertThat(invocationNames(Fixture::class.java, "alongsideAResolvedParameter"))
            .isEqualTo(listOf("[1] value=1"))
    }

    @Test
    fun testPositionalPlaceholdersCountGeneratedParametersOnly() {
        assertThat(invocationNames(Fixture::class.java, "positionalAroundAResolvedParameter"))
            .isEqualTo(listOf("1 and true", "1 and false", "2 and true", "2 and false"))
    }

    class Fixture {

        @GeneratedParametersTest
        fun defaultName(@IntRangeSource(min = 1, max = 2) value: Int) {
        }

        @GeneratedParametersTest(name = "value {0} of the run")
        fun customName(@IntRangeSource(min = 1, max = 2) value: Int) {
        }

        /** TestInfo is resolved by JUnit, so it is neither named nor counted. */
        @GeneratedParametersTest
        fun alongsideAResolvedParameter(
            @IntRangeSource(min = 1, max = 1) value: Int,
            testInfo: TestInfo
        ) {
        }

        /** The resolved parameter sits between the generated ones without shifting {0} and {1}. */
        @GeneratedParametersTest(name = "{0} and {1}")
        fun positionalAroundAResolvedParameter(
            @IntRangeSource(min = 1, max = 2) value: Int,
            testInfo: TestInfo,
            flag: Boolean
        ) {
        }
    }
}
