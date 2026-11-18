package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.wesleyhome.test.jupiter.consumer.JavaConsumerFilters
import com.wesleyhome.test.jupiter.java.filter.JavaParametersFilters
import org.junit.jupiter.api.Test

/** The cases no fixture compiled as a consumer's build can reach, bound against a layout built by hand. */
class FilterBindingTest {

    /**
     * 4.1.0 matched `other` and `end` against `arg0` and `arg1`, and failed, so position never decided
     * this pairing and doesn't now.
     */
    @Test
    fun testAFilterWithCompiledNamesBindsByNameToATestWithout() {
        val filter = JavaParametersFilters::class.java.getMethod("otherThenEnd", Int::class.java, Int::class.java)
        assertThat(slotsOf(filter, positionsNamed("start", "end"))).isEqualTo(listOf(null, 1))
    }

    /** Neither filter was paired by position before, so neither is said to be now: both just fail to bind. */
    @Test
    fun testOnlyAPairingByPositionIsWarnedAbout() {
        val compiledNames = JavaParametersFilters::class.java.getMethod("otherThenEnd", Int::class.java, Int::class.java)
        assertThat(FilterBinding(compiledNames, positionsNamed("low", "high")).boundByPositionBecause).isNull()
        val debugNames = JavaConsumerFilters::class.java.getMethod("sharedOrdered", Int::class.java, Int::class.java)
        assertThat(FilterBinding(debugNames, namesNamed("low", "high")).boundByPositionBecause).isNull()
        assertThat(FilterBinding(debugNames, positionsNamed("low", "high")).boundByPositionBecause)
            .isEqualTo(
                "the test has no parameter named [start, end]. Name the filter's parameters as the test does to " +
                    "bind by name."
            )
    }

    /** Kotlin writes `value` into the bridge's debug information too, which would let both bind. */
    @Test
    fun testABridgeForAGenericOverrideDoesNotBindByName() {
        val methods = GenericOverride::class.java.declaredMethods.filter { it.name == "check" }
        val layout = namesNamed("value")
        assertThat(slotsOf(methods.single { it.isBridge }, layout)).isEqualTo(listOf(null))
        assertThat(slotsOf(methods.single { !it.isBridge }, layout)).isEqualTo(listOf(0))
    }

    private fun slotsOf(method: java.lang.reflect.Method, layout: GeneratedParameterLayout): List<Int?> =
        FilterBinding(method, layout).let { binding -> (0 until method.parameterCount).map(binding::slotOf) }

    /** A Java test compiled without `-parameters`: display names are positions, its debug information has names. */
    private fun positionsNamed(vararg declared: String) = layoutOf(Array(declared.size) { "arg$it" }, declared)

    private fun namesNamed(vararg declared: String) = layoutOf(arrayOf(*declared), declared)

    private fun layoutOf(names: Array<String>, declared: Array<out String>) = GeneratedParameterLayout(
        names,
        Array(names.size) { Int::class },
        BooleanArray(names.size),
        IntArray(names.size) { it },
        arrayOf(*declared),
        true
    )

    interface Check<T> {
        fun check(value: T): Boolean
    }

    object GenericOverride : Check<Int> {
        override fun check(value: Int): Boolean = value > 1
    }
}
