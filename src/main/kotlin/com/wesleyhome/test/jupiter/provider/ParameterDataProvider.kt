package com.wesleyhome.test.jupiter.provider

interface ParameterDataProvider<T : Any> {

    fun providesDataFor(testParameter: TestParameter): Boolean
    fun createParameterOptionsData(testParameter: TestParameter): List<T?>
}
