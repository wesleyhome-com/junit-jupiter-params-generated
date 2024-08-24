package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.IntSource

class IntValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Int, IntSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Int?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
