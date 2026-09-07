package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.math.BigDecimal

internal abstract class AnnotatedNumberRangeParameterDataProviderTest
<P : AbstractAnnotatedNumberRangeDataProvider<T, A>, T : Number, A : Annotation> :
    AnnotatedParameterDataProviderTest<P, T, A>() {
    protected fun assertParameters(
        testParameter: TestParameter,
        min: T,
        max: T,
        increment: T,
        ascending: Boolean
    ) {
        val incrementDouble = increment.toDouble()
        val minDouble = min.toDouble()
        val maxDouble = max.toDouble()
        val errors = mutableListOf<String>().addIf("Increment must be greater than zero") {
            incrementDouble <= 0.0
        }.addIf("Min value cannot be greater than max value") {
            minDouble >= maxDouble
        }.toList()
        if (errors.isNotEmpty()) {
            testCreateParameterOptionsDataWithException(testParameter) {
                it.isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage(errors.joinToString(", "))
            }
            return
        } else {
            val expected = expectedRange(min, max, increment).let {
                if (ascending) {
                    it
                } else {
                    it.reversed()
                }
            }
            testCreateParameterOptionsData(testParameter, false) {
                it.isEqualTo(expected)
            }
        }
    }

    /**
     * Derived independently of the provider: walk indices outward from [min] and stop once the
     * value passes [max]. Computing the expectation with the same accumulate-and-add loop the
     * provider used made this assertion a tautology that drifted along with it.
     */
    private fun expectedRange(min: T, max: T, increment: T): List<T> {
        val minDecimal = BigDecimal(min.toString())
        val maxDecimal = BigDecimal(max.toString())
        val incrementDecimal = BigDecimal(increment.toString())
        return generateSequence(0L) { it + 1 }
            .map { index -> minDecimal + incrementDecimal * BigDecimal.valueOf(index) }
            .takeWhile { it <= maxDecimal }
            .map { convert(it) }
            .toList()
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        val first: T = convert(1)
        val second: T = convert(11)
        val third: T = convert(2)
        return createAnnotatedTestParameter(first, second, third, true)
    }

    private fun convert(value: Number): T = provider.convert(value)

    private fun <T> MutableList<T>.addIf(value: T, condition: () -> Boolean): MutableList<T> {
        if (condition()) {
            add(value)
        }
        return this
    }
}
