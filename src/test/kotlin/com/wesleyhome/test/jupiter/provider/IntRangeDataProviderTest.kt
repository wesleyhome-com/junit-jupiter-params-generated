package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.IntSource

class IntRangeDataProviderTest :
    AnnotatedNumberRangeParameterDataProviderTest<IntRangeDataProvider, Int, IntRangeSource>() {

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @IntSource([1, 5, 10])
        min: Int,
        @IntSource([2, 10, 20])
        max: Int,
        @IntSource([0, 1, 2])
        increment: Int,
        ascending: Boolean
    ) {
        val testParameter = createAnnotatedTestParameter(min, max, increment, ascending)
        assertParameters(testParameter, min, max, increment, ascending)
    }
}
