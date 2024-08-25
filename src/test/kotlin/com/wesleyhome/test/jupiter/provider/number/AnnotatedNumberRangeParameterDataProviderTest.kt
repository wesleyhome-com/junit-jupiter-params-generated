package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.provider.AnnotatedParameterDataProviderTest
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.step

abstract class AnnotatedNumberRangeParameterDataProviderTest
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
        when {
            incrementDouble <= 0.0 -> {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage("increment must be greater than 0")
                }
            }

            minDouble >= maxDouble -> {
                testCreateParameterOptionsDataWithException(testParameter) {
                    it.isInstanceOf(IllegalArgumentException::class.java)
                        .hasMessage("min must be less than max")
                }
            }

            else -> {
                val expected = (minDouble..maxDouble step incrementDouble).map { convert(it) }.toList().let {
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
    }

    override fun createTrueProvidesForTestParameter(): TestParameter {
        val first: T = convert(1)
        val second: T = convert(11)
        val third: T = convert(2)
        return createAnnotatedTestParameter(first, second, third, true)
    }

    private fun convert(value: Number): T = provider.convert(value)
}
