package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource
import com.wesleyhome.test.jupiter.annotations.number.LongSource

internal class LongRangeDataProviderTest :
    AnnotatedNumberRangeParameterDataProviderTest<LongRangeDataProvider, Long, LongRangeSource>() {

    @GeneratedParametersTest
    fun testCreateParametersOptionsData(
        @LongSource([1, 5, 10])
        min: Long,
        @LongSource([2, 10, 20])
        max: Long,
        @LongSource([0, 1, 2])
        increment: Long,
        ascending: Boolean
    ) {
        val testParameter = createAnnotatedTestParameter(min, max, increment, ascending)
        assertParameters(testParameter, min, max, increment, ascending)
    }
}
