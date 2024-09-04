package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DefaultProvider

@DefaultProvider
class BooleanParameterDataProvider : AbstractParameterDataProvider<Boolean>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<Boolean?> {
        return listOf(true, false)
    }

}
