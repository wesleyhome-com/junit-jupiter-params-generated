package com.wesleyhome.test.jupiter.provider.number

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.number.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test

/**
 * Exact expectations for ranges that an accumulating `current + step` loop gets wrong.
 *
 * The fixtures name each invocation after the value it received, so these assert the generated
 * sequence through the output a user actually reads. `Double.toString` renders the shortest
 * representation that round-trips, so accumulated error stays visible: a drifting range would show
 * `0.30000000000000004`, not `0.3`. [testDoubleRangeValuesAreExact] additionally pins the typed
 * values, which the rendered form cannot.
 */
class NumberRangePrecisionTest {

    @Test
    fun testDoubleRangeDoesNotAccumulateError() {
        assertThat(invocationNames(Fixture::class.java, "doubleRange")).isEqualTo(
            listOf("0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0")
        )
    }

    @Test
    fun testDoubleRangeValuesAreExact() {
        val annotation = DoubleRangeSource::class.constructors.first().call(0.0, 1.0, 0.1, true)
        val values = DoubleRangeDataProvider().createParameterOptionsData(
            TestParameter(name = "value", type = Double::class, annotations = listOf(annotation))
        )
        assertThat(values).isEqualTo(listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
    }

    @Test
    fun testFloatRangeDoesNotAccumulateError() {
        assertThat(invocationNames(Fixture::class.java, "floatRange")).isEqualTo(
            listOf("0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0")
        )
    }

    @Test
    fun testLongRangeKeepsPrecisionBeyondDoubleMantissa() {
        assertThat(invocationNames(Fixture::class.java, "longRangePastTheMantissa")).isEqualTo(
            listOf(
                "9007199254740990",
                "9007199254740991",
                "9007199254740992",
                "9007199254740993",
                "9007199254740994",
            )
        )
    }

    @Test
    fun testDescendingRangeIsTheExactReverse() {
        assertThat(invocationNames(Fixture::class.java, "descendingRange"))
            .isEqualTo(listOf("0.5", "0.4", "0.3", "0.2", "0.1", "0.0"))
    }

    @Test
    fun testIncrementThatDoesNotDivideTheRangeStopsBeforeMax() {
        assertThat(invocationNames(Fixture::class.java, "unevenIncrement"))
            .isEqualTo(listOf("0.0", "0.3", "0.6", "0.9"))
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun doubleRange(@DoubleRangeSource(min = 0.0, max = 1.0, increment = 0.1) value: Double) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun floatRange(@FloatRangeSource(min = 0.0f, max = 1.0f, increment = 0.1f) value: Float) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun longRangePastTheMantissa(
            @LongRangeSource(min = 9_007_199_254_740_990L, max = 9_007_199_254_740_994L) value: Long
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun descendingRange(
            @DoubleRangeSource(min = 0.0, max = 0.5, increment = 0.1, ascending = false) value: Double
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun unevenIncrement(@DoubleRangeSource(min = 0.0, max = 1.0, increment = 0.3) value: Double) {
        }
    }
}
