package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.provider.BooleanParameterDataProvider
import com.wesleyhome.test.jupiter.provider.EnumParameterDataProvider
import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

internal object DataProviderRegistry {
    val defaultDataProviders by lazy {
        listOf(BooleanParameterDataProvider(), EnumParameterDataProvider())
    }

    /**
     * Shared across every test method in the JVM, so it has to tolerate concurrent access:
     * `junit.jupiter.execution.parallel.enabled` puts several engine threads through here at once,
     * and `computeIfAbsent` on a plain HashMap can corrupt the table or spin when it resizes
     * under a concurrent write.
     */
    private val providers = ConcurrentHashMap<String, ParameterDataProvider<*>>()

    fun createInstance(
        annotationClassName: String,
        providerClass: KClass<out ParameterDataProvider<*>>
    ): ParameterDataProvider<*> =
        providers.computeIfAbsent(annotationClassName) {
            val constructor = providerClass.constructors.first()
            constructor.call()
        }

}
