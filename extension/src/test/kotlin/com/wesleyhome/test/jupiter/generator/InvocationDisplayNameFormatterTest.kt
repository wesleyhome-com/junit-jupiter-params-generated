package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.generator.GeneratedParameterLayout.Companion.NO_SLOT
import org.junit.jupiter.api.Test

class InvocationDisplayNameFormatterTest {

    /**
     * Two generated parameters, `left` and `right`. `right` sits at *method* parameter index 2, so
     * something JUnit resolves occupies index 1. That gap is deliberate: it proves `{0}` and `{1}`
     * count generated slots rather than method parameter positions.
     */
    private val layout = GeneratedParameterLayout(
        arrayOf("left", "right"),
        intArrayOf(0, NO_SLOT, 1)
    )

    private val values: Array<Any?> = arrayOf(1, "b")

    private fun format(
        pattern: String,
        values: Array<Any?> = this.values,
        layout: GeneratedParameterLayout = this.layout
    ) = InvocationDisplayNameFormatter
        .compile(pattern, "someTest(int, String)", layout)
        .format(7, values)

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
        val single = GeneratedParameterLayout(arrayOf("value"), intArrayOf(0))
        assertThat(format("[{index}] {argumentsWithNames}", arrayOf(null), single))
            .isEqualTo("[7] value=null")
    }

    @Test
    fun testArrayValueIsRenderedByContent() {
        val single = GeneratedParameterLayout(arrayOf("value"), intArrayOf(0))
        assertThat(format("[{index}] {arguments}", arrayOf(arrayOf(1, 2)), single))
            .isEqualTo("[7] [1, 2]")
    }

    @Test
    fun testNoGeneratedArgumentsLeavesNoTrailingSeparator() {
        val none = GeneratedParameterLayout(emptyArray(), IntArray(0))
        assertThat(format("[{index}] {argumentsWithNames}", emptyArray(), none)).isEqualTo("[7]")
    }
}
