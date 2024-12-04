package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.DoubleSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter

internal class DoubleValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Double, DoubleSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Double?> {
        return findAnnotation(testParameter)!!.values.toList()
    }
}
