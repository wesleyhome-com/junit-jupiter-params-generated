package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.size
import com.wesleyhome.test.jupiter.provider.TestModel
import org.junit.jupiter.api.Test
import java.util.Collections

class ArgumentParametersTest {

    @Test
    fun testNoOptionsYieldsSingleEmptyArgument() {
        val arguments = ArgumentParameters(emptyList()).toList()
        assertThat(arguments).size().isEqualTo(1)
        assertThat(arguments.first().toList()).isEmpty()
    }

    @Test
    fun testTestMethodWithoutParametersIsGeneratedOnce() {
        val arguments = ParametersGenerator(TestModel(testParameters = emptyList())).arguments().toList()
        assertThat(arguments).size().isEqualTo(1)
    }

    @Test
    fun testCartesianProductSize() {
        val arguments = ArgumentParameters(listOf(listOf(1, 2, 3), listOf("a", "b"))).toList()
        assertThat(arguments).size().isEqualTo(6)
        assertThat(arguments.map { it.toList() }).isEqualTo(
            listOf(
                listOf(1, "a"), listOf(1, "b"),
                listOf(2, "a"), listOf(2, "b"),
                listOf(3, "a"), listOf(3, "b"),
            )
        )
    }

    @Test
    fun testRightMostParameterVariesFastestAcrossThreeDimensions() {
        val arguments = ArgumentParameters(listOf(listOf(1, 2), listOf("a", "b"), listOf(true, false)))
        assertThat(arguments.map { it.toList() }).isEqualTo(
            listOf(
                listOf(1, "a", true), listOf(1, "a", false),
                listOf(1, "b", true), listOf(1, "b", false),
                listOf(2, "a", true), listOf(2, "a", false),
                listOf(2, "b", true), listOf(2, "b", false),
            )
        )
    }

    /** The cursor used to live on the instance, so a second pass silently produced nothing. */
    @Test
    fun testCanBeIteratedMoreThanOnce() {
        val arguments = ArgumentParameters(listOf(listOf(1, 2, 3), listOf("a", "b")))
        val first = arguments.map { it.toList() }
        val second = arguments.map { it.toList() }
        assertThat(second).isEqualTo(first)
    }

    @Test
    fun testValuesAtAgreesWithIteration() {
        val arguments = ArgumentParameters(listOf(listOf(1, 2, 3), listOf("a", "b")))
        val iterated = arguments.map { it.toList() }
        val indexed = (0 until arguments.totalPermutations).map { arguments.valuesAt(it).toList() }
        assertThat(indexed).isEqualTo(iterated)
    }

    @Test
    fun testAnEmptyOptionListYieldsNoInvocations() {
        val arguments = ArgumentParameters(listOf(listOf(1, 2), emptyList()))
        assertThat(arguments.totalPermutations).isEqualTo(0L)
        assertThat(arguments.toList()).isEmpty()
    }

    /**
     * The product used to be computed with `*`, which wrapped to a small or negative count and made
     * the template generate the wrong number of invocations instead of failing.
     *
     * `nCopies` gives a list with a size and no elements to allocate, so this stays cheap.
     */
    @Test
    fun testOverflowingProductSaturatesInsteadOfWrapping() {
        val large = List(3) { Collections.nCopies(2_000_000, 0) }
        assertThat(ArgumentParameters(large).totalPermutations).isEqualTo(8_000_000_000_000_000_000L)

        val overflowing = List(10) { Collections.nCopies(1_000_000, 0) }
        assertThat(ArgumentParameters(overflowing).totalPermutations).isEqualTo(Long.MAX_VALUE)
    }
}
