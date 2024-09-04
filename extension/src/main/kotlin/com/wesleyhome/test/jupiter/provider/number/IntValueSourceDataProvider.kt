package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter

class IntValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Int, IntSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Int?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
