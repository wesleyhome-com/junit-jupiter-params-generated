package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.DoubleSource
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest

class DoubleRangeDataProviderTest :
    AnnotatedNumberRangeParameterDataProviderTest<DoubleRangeDataProvider, Double, DoubleRangeSource>() {

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @DoubleSource([1.0, 2.0, 5.0])
        min: Double,
        @DoubleSource([2.0, 3.0, 4.0])
        max: Double,
        @DoubleSource([-.1, .5, 1.0])
        increment: Double,
        ascending: Boolean
    ) {
        val testParameter = createAnnotatedTestParameter(min, max, increment, ascending)
        assertParameters(testParameter, min, max, increment, ascending)
    }

}
