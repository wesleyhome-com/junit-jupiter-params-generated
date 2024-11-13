package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.provider.BooleanParameterDataProvider
import com.wesleyhome.test.jupiter.provider.EnumParameterDataProvider
import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import kotlin.reflect.KClass

internal object DataProviderRegistry {
    val defaultDataProviders by lazy {
        listOf(BooleanParameterDataProvider(), EnumParameterDataProvider())
    }
    private val providers = mutableMapOf<String, ParameterDataProvider<*>>()

    fun createInstance(
        annotationClassName: String,
        providerClass: KClass<out ParameterDataProvider<*>>
    ): ParameterDataProvider<*> =
        providers.computeIfAbsent(annotationClassName) {
            val constructor = providerClass.constructors.first()
            constructor.call()
        }

}
