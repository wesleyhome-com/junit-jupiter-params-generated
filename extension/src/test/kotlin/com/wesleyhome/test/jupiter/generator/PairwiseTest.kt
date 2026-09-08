package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import com.wesleyhome.test.jupiter.InvalidStrategyException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy.PAIRWISE
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.reportEntries
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test

/**
 * Pairwise trades combinations for invocations: every pair of parameters still appears with every
 * pair of their values, and combinations needing three specific values at once are given up.
 */
class PairwiseTest {

    @Test
    fun testEveryPairOfValuesAppearsInSomeInvocation() {
        val rows = rowsOf(Fixture::class.java, "threeByThree")
        assertThat(uncoveredPairs(rows, 3)).isEmpty()
    }

    @Test
    fun testFewerInvocationsThanTheProduct() {
        val rows = rowsOf(Fixture::class.java, "threeByThree")
        // 27 combinations. Nine is the theoretical minimum; greedy selection is not minimal, so this
        // asserts the saving rather than optimality.
        assertThat(rows.size in 9..12).isTrue()
    }

    /** The saving comes from parameter count, so two parameters cannot be reduced at all. */
    @Test
    fun testTwoParametersAreTheSameAsCartesian() {
        assertThat(invocationNames(Fixture::class.java, "twoParameters")).hasSize(9)
    }

    @Test
    fun testSixParametersCollapseSharply() {
        val rows = rowsOf(Fixture::class.java, "sixParameters")
        assertThat(uncoveredPairs(rows, 6)).isEmpty()
        // 2^6 is 64 combinations; every pair of six booleans fits in far fewer.
        assertThat(rows.size <= 12).isTrue()
    }

    /**
     * The guarantee has to hold over what the filter accepts. Covering first and filtering after
     * would drop whichever rows happened to carry a pair, leaving it uncovered and unreported.
     */
    @Test
    fun testTheGuaranteeHoldsUnderAFilter() {
        val rows = rowsOf(Fixture::class.java, "filtered")
        assertThat(rows.all { it[0].trim().toInt() <= it[1].trim().toInt() }).isTrue()
        assertThat(uncoveredFeasiblePairs(rows)).isEmpty()
    }

    @Test
    fun testSelectionIsIdenticalOnEveryRun() {
        val first = invocationNames(Fixture::class.java, "threeByThree")
        val second = invocationNames(Fixture::class.java, "threeByThree")
        assertThat(second).isEqualTo(first)
    }

    /**
     * Pinned so a change to the selection cannot quietly change what a suite runs. Ten rows for a
     * 27-combination product; nine is the theoretical minimum, and greedy selection does not reach
     * it. Rows are emitted in generation order rather than the order they were selected.
     */
    @Test
    fun testTheChosenCombinationsArePinned() {
        assertThat(invocationNames(Fixture::class.java, "threeByThree")).isEqualTo(
            listOf(
                "1, 1, 1", "1, 2, 2", "1, 3, 3",
                "2, 1, 2", "2, 2, 1", "2, 2, 3", "2, 3, 2",
                "3, 1, 3", "3, 2, 2", "3, 3, 1"
            )
        )
    }

    @Test
    fun testTheStrategyIsNamedInTheReportSummary() {
        val entries = reportEntries(Fixture::class.java, "threeByThree")
        assertThat(entries.single()["generated.strategy"]).isEqualTo("PAIRWISE")
        assertThat(entries.single()["generated.invocations"]).isEqualTo("27")
    }

    @Test
    fun testAParameterWithTooManyValuesIsReported() {
        assertThat(executionFailure(Fixture::class.java, "hugeParameter"))
            .isInstanceOf(InvalidStrategyException::class)
            .hasMessage(
                "PAIRWISE supports at most 65536 values per parameter, but one generates 70000. " +
                    "Pairwise reduces invocations when there are many parameters, not when there are many values."
            )
    }

    @Test
    fun testAFilterRejectingEverythingIsStillReported() {
        assertThat(executionFailure(Fixture::class.java, "filteredToNothing"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage(
                "All 9 generated combinations were rejected by [filteredToNothing_filter]. " +
                    "Relax a filter, or widen the values the parameters generate."
            )
    }

    @Test
    fun testASingleParameterStillRunsEveryValue() {
        assertThat(invocationNames(Fixture::class.java, "oneParameter")).isEqualTo(listOf("1", "2", "3"))
    }

    private fun rowsOf(fixture: Class<*>, method: String): List<List<String>> =
        invocationNames(fixture, method).map { row -> row.split(", ") }

    /** Every combination of every two positions must appear in some row. */
    private fun uncoveredPairs(rows: List<List<String>>, parameters: Int): Set<String> {
        val present = rows.flatMap { row -> pairsOf(row) }.toSet()
        val expected = mutableSetOf<String>()
        for (a in 0 until parameters) {
            for (b in a + 1 until parameters) {
                rows.map { it[a] }.distinct().forEach { va ->
                    rows.map { it[b] }.distinct().forEach { vb -> expected += "$a=$va,$b=$vb" }
                }
            }
        }
        return expected - present
    }

    private fun uncoveredFeasiblePairs(rows: List<List<String>>): Set<String> {
        val present = rows.flatMap { row -> pairsOf(row) }.toSet()
        val expected = mutableSetOf<String>()
        for (a in 1..3) for (b in 1..3) {
            if (a <= b) expected += "0=$a,1=$b"
        }
        return expected - present
    }

    private fun pairsOf(row: List<String>): List<String> = buildList {
        for (a in row.indices) {
            for (b in a + 1 until row.size) {
                add("$a=${row[a]},$b=${row[b]}")
            }
        }
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
        fun threeByThree(
            @IntRangeSource(min = 1, max = 3) a: Int,
            @IntRangeSource(min = 1, max = 3) b: Int,
            @IntRangeSource(min = 1, max = 3) c: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
        fun twoParameters(
            @IntRangeSource(min = 1, max = 3) a: Int,
            @IntRangeSource(min = 1, max = 3) b: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
        fun sixParameters(a: Boolean, b: Boolean, c: Boolean, d: Boolean, e: Boolean, f: Boolean) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
        fun oneParameter(@IntRangeSource(min = 1, max = 3) a: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE, filters = ["startBeforeEnd"])
        fun filtered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int,
            @IntRangeSource(min = 1, max = 3) other: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
        fun filteredToNothing(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
        fun hugeParameter(
            @IntRangeSource(min = 1, max = 70000) a: Int,
            @StringSource(["x", "y"]) b: String
        ) {
        }

        companion object {
            @JvmStatic
            fun startBeforeEnd(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun filteredToNothing_filter(start: Int, end: Int): Boolean = false
        }
    }
}
