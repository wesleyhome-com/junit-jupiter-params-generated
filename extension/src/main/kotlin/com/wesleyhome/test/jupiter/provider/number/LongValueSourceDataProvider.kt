package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.LongSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter

class LongValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Long, LongSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
