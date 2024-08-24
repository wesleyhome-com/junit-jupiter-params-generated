package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LongSource

class LongValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Long, LongSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
