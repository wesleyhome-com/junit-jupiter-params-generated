package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.full.isSubclassOf

internal class EnumParameterDataProvider : ParameterDataProvider<Enum<*>> {
    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return testParameter.type.isSubclassOf(Enum::class)
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<Enum<*>?> {
        return testParameter.type.java.enumConstants.map { it as Enum<*> }
    }
}
