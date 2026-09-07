package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class InvocationDisplayNameFormatterTest {

    private val arguments = listOf(
        GeneratedArgument(parameterIndex = 0, name = "left", value = 1),
        GeneratedArgument(parameterIndex = 2, name = "right", value = "b"),
    )

    private fun format(pattern: String, arguments: List<GeneratedArgument> = this.arguments) =
        InvocationDisplayNameFormatter.format(pattern, 7, arguments, "someTest(int, String)")

    @Test
    fun testDefaultPattern() {
        assertThat(format("[{index}] {argumentsWithNames}")).isEqualTo("[7] left=1, right=b")
    }

    @Test
    fun testArgumentsWithoutNames() {
        assertThat(format("[{index}] {arguments}")).isEqualTo("[7] 1, b")
    }

    @Test
    fun testMethodDisplayName() {
        assertThat(format("{displayName} #{index}")).isEqualTo("someTest(int, String) #7")
    }

    @Test
    fun testPositionalPlaceholdersCountGeneratedArgumentsNotParameters() {
        assertThat(format("{0} then {1}")).isEqualTo("1 then b")
    }

    @Test
    fun testUnknownPlaceholderIsLeftAlone() {
        assertThat(format("[{index}] {nope} {9}")).isEqualTo("[7] {nope} {9}")
    }

    @Test
    fun testNullValueIsRendered() {
        val withNull = listOf(GeneratedArgument(parameterIndex = 0, name = "value", value = null))
        assertThat(format("[{index}] {argumentsWithNames}", withNull)).isEqualTo("[7] value=null")
    }

    @Test
    fun testArrayValueIsRenderedByContent() {
        val withArray = listOf(GeneratedArgument(parameterIndex = 0, name = "value", value = arrayOf(1, 2)))
        assertThat(format("[{index}] {arguments}", withArray)).isEqualTo("[7] [1, 2]")
    }

    @Test
    fun testNoGeneratedArgumentsLeavesNoTrailingSeparator() {
        assertThat(format("[{index}] {argumentsWithNames}", emptyList())).isEqualTo("[7]")
    }
}
