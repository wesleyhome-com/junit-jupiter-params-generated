package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.FloatSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter

class FloatValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Float, FloatSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Float?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
