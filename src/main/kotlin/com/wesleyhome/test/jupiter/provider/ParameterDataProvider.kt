package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.KClass

interface ParameterDataProvider<T : Any> {

    fun providesDataFor(testParameter: TestParameter): Boolean {
        return testParameter.type == dataProviderFor()
    }
    fun dataProviderFor() : KClass<T>
    fun createParameterOptionsData(testParameter: TestParameter): List<T?>
}
