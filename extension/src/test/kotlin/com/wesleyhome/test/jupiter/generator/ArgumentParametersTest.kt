package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEmpty
import assertk.assertions.size
import com.wesleyhome.test.jupiter.provider.TestModel
import org.junit.jupiter.api.Test

class ArgumentParametersTest {

    @Test
    fun testNoOptionsYieldsSingleEmptyArgument() {
        val arguments = ArgumentParameters(emptyList()).toList()
        assertThat(arguments).size().isEqualTo(1)
        assertThat(arguments.first().get().toList()).isEmpty()
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
        assertThat(arguments.map { it.get().toList() }).isEqualTo(
            listOf(
                listOf(1, "a"), listOf(1, "b"),
                listOf(2, "a"), listOf(2, "b"),
                listOf(3, "a"), listOf(3, "b"),
            )
        )
    }
}
