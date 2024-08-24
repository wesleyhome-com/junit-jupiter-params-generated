package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.StringSource
import kotlin.reflect.KClass

class StringValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<String, StringSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<String?> =
        findAnnotation(testParameter)!!.values.toList()

}
