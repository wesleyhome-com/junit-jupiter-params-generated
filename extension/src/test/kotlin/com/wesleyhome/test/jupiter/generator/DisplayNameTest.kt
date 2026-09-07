package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInfo

class DisplayNameTest {

    @GeneratedParametersTest
    fun testDefaultDisplayName(
        @IntRangeSource(min = 1, max = 2) value: Int,
        testInfo: TestInfo
    ) {
        defaults += testInfo.displayName
    }

    @GeneratedParametersTest(name = "value {0} of the run")
    fun testCustomDisplayName(
        @IntRangeSource(min = 1, max = 2) value: Int,
        testInfo: TestInfo
    ) {
        customs += testInfo.displayName
    }

    /** Only generated parameters are named; the resolved TestInfo is not one of them. */
    @GeneratedParametersTest
    fun testResolvedParametersAreNotListed(
        @IntRangeSource(min = 1, max = 1) value: Int,
        testInfo: TestInfo
    ) {
        mixed += testInfo.displayName
    }

    companion object {
        private val defaults = mutableListOf<String>()
        private val customs = mutableListOf<String>()
        private val mixed = mutableListOf<String>()

        @JvmStatic
        @AfterAll
        fun assertDisplayNames() {
            assertThat(defaults).isEqualTo(listOf("[1] value=1", "[2] value=2"))
            assertThat(customs).isEqualTo(listOf("value 1 of the run", "value 2 of the run"))
            assertThat(mixed).isEqualTo(listOf("[1] value=1"))
        }
    }
}
