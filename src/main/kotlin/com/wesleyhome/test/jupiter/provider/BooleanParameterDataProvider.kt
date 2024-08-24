package com.wesleyhome.test.jupiter.provider

class BooleanParameterDataProvider : AbstractParameterDataProvider<Boolean>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<Boolean?> {
        return listOf(true, false)
    }

}
