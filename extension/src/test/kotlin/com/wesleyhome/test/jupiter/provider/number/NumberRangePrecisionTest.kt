package com.wesleyhome.test.jupiter.provider.number

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.number.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource
import org.junit.jupiter.api.AfterAll

/**
 * Exact expectations for ranges that an accumulating `current + step` loop gets wrong.
 *
 * These run end to end so the annotation is read the way the extension really reads it, and every
 * generated value is recorded in order and asserted once the range has been walked.
 */
class NumberRangePrecisionTest {

    @GeneratedParametersTest
    fun testDoubleRangeDoesNotAccumulateError(
        @DoubleRangeSource(min = 0.0, max = 1.0, increment = 0.1) value: Double
    ) {
        doubles += value
    }

    @GeneratedParametersTest
    fun testFloatRangeDoesNotAccumulateError(
        @FloatRangeSource(min = 0.0f, max = 1.0f, increment = 0.1f) value: Float
    ) {
        floats += value
    }

    @GeneratedParametersTest
    fun testLongRangeKeepsPrecisionBeyondDoubleMantissa(
        @LongRangeSource(min = 9_007_199_254_740_990L, max = 9_007_199_254_740_994L) value: Long
    ) {
        longs += value
    }

    @GeneratedParametersTest
    fun testDescendingRangeIsTheExactReverse(
        @DoubleRangeSource(min = 0.0, max = 0.5, increment = 0.1, ascending = false) value: Double
    ) {
        descending += value
    }

    @GeneratedParametersTest
    fun testIncrementThatDoesNotDivideTheRangeStopsBeforeMax(
        @DoubleRangeSource(min = 0.0, max = 1.0, increment = 0.3) value: Double
    ) {
        uneven += value
    }

    companion object {
        private val doubles = mutableListOf<Double>()
        private val floats = mutableListOf<Float>()
        private val longs = mutableListOf<Long>()
        private val descending = mutableListOf<Double>()
        private val uneven = mutableListOf<Double>()

        @JvmStatic
        @AfterAll
        fun assertGeneratedValues() {
            assertThat(doubles).isEqualTo(listOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
            assertThat(floats).isEqualTo(listOf(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f))
            assertThat(longs).isEqualTo(
                listOf(
                    9_007_199_254_740_990L,
                    9_007_199_254_740_991L,
                    9_007_199_254_740_992L,
                    9_007_199_254_740_993L,
                    9_007_199_254_740_994L,
                )
            )
            assertThat(descending).isEqualTo(listOf(0.5, 0.4, 0.3, 0.2, 0.1, 0.0))
            assertThat(uneven).isEqualTo(listOf(0.0, 0.3, 0.6, 0.9))
        }
    }
}
