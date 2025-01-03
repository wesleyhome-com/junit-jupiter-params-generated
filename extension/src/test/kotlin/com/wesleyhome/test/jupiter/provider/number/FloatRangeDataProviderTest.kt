package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.number.FloatSource

internal class FloatRangeDataProviderTest :
    AnnotatedNumberRangeParameterDataProviderTest<FloatRangeDataProvider, Float, FloatRangeSource>() {

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @FloatSource([1f, 5f, 15f])
        min: Float,
        @FloatSource([2f, 3f, 4f])
        max: Float,
        @FloatSource([0f, 1f])
        increment: Float,
        ascending: Boolean
    ) {
        val testParameter = createAnnotatedTestParameter(min, max, increment, ascending)
        assertParameters(testParameter, min, max, increment, ascending)
    }

}
