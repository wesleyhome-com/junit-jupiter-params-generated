package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DefaultProvider
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

@DefaultProvider
class EnumParameterDataProvider : ParameterDataProvider<Enum<*>> {
    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return testParameter.type.isSubclassOf(Enum::class)
    }

    override fun dataProviderFor(): KClass<Enum<*>> {
        return Enum::class
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<Enum<*>?> {
        return testParameter.type.java.enumConstants.map { it as Enum<*> }
    }
}
