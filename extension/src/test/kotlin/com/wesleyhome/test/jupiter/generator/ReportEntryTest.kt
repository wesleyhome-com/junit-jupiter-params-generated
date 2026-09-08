package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.wesleyhome.test.jupiter.GeneratedParametersReport
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.reportEntries
import org.junit.jupiter.api.Test

/**
 * The display name is one string, gets truncated by report viewers, and nothing downstream can
 * query it. Report entries are the structured channel JUnit already provides.
 */
class ReportEntryTest {

    private val summary = GeneratedParametersReport.SUMMARY_PROPERTY
    private val values = GeneratedParametersReport.VALUES_PROPERTY

    @Test
    fun testSummaryIsPublishedByDefault() {
        assertThat(reportEntries(Fixture::class.java, "product")).isEqualTo(
            listOf(mapOf("generated.invocations" to "6", "generated.parameters" to "a=3 x b=2"))
        )
    }

    @Test
    fun testSummaryCanBeTurnedOff() {
        assertThat(reportEntries(Fixture::class.java, "product", summary to "false")).hasSize(0)
    }

    /** Six invocations would mean six entries, which is why this is not the default. */
    @Test
    fun testValuesAreNotPublishedByDefault() {
        val entries = reportEntries(Fixture::class.java, "product")
        assertThat(entries.none { it.keys.any { key -> key == "generated.a" } }).isTrue()
    }

    @Test
    fun testValuesArePublishedWhenEnabled() {
        val entries = reportEntries(Fixture::class.java, "product", summary to "false", values to "true")
        assertThat(entries).hasSize(6)
        assertThat(entries.first()).isEqualTo(mapOf("generated.a" to "1", "generated.b" to "1"))
        assertThat(entries.last()).isEqualTo(mapOf("generated.a" to "3", "generated.b" to "2"))
    }

    /** Parameters another resolver owns are not generated, so they are not reported either. */
    @Test
    fun testOnlyGeneratedParametersAreReported() {
        val entries = reportEntries(Fixture::class.java, "withResolvedParameter", summary to "false", values to "true")
        assertThat(entries).hasSize(2)
        assertThat(entries.first()).isEqualTo(mapOf("generated.value" to "1"))
    }

    /** A report entry value may not be blank, so the parameters key is omitted rather than empty. */
    @Test
    fun testSummaryForAMethodWithNoGeneratedParameters() {
        assertThat(reportEntries(Fixture::class.java, "noParameters"))
            .isEqualTo(listOf(mapOf("generated.invocations" to "1")))
    }

    @Test
    fun testAMalformedFlagIsReported() {
        assertThat(executionFailure(Fixture::class.java, "product", summary to "yes please"))
            .isInstanceOf(IllegalArgumentException::class)
    }

    class Fixture {

        @GeneratedParametersTest(name = "{index}")
        fun product(
            @IntRangeSource(min = 1, max = 3) a: Int,
            @IntRangeSource(min = 1, max = 2) b: Int
        ) {
        }

        @GeneratedParametersTest(name = "{index}")
        fun noParameters() {
        }

        @GeneratedParametersTest(name = "{index}")
        fun withResolvedParameter(
            @IntRangeSource(min = 1, max = 2) value: Int,
            testInfo: org.junit.jupiter.api.TestInfo
        ) {
        }
    }
}
