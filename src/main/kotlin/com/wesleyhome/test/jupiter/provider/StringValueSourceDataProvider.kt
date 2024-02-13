package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.StringSource

object StringValueSourceDataProvider : AbstractParameterDataProvider<String>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<String?> =
        findAnnotation(testParameter)!!.values.toList()

    override fun providesDataFor(testParameter: TestParameter): Boolean =
        super.providesDataFor(testParameter) && findAnnotation(testParameter) != null

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is StringSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as StringSource
            }
        }

}
