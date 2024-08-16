package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import io.github.classgraph.ClassGraph

object DataProviderRegistry {
    val dataProviders: List<ParameterDataProvider<*>> = ClassGraph()
        .enableClassInfo()
        .scan().use { scanResult ->
            scanResult.allClasses.filterNot { it.isAbstract }
                .filter { it.implementsInterface(ParameterDataProvider::class.java) }
                .map {
                    try {
                        it.loadClass().getDeclaredConstructor().newInstance() as ParameterDataProvider<*>
                    } catch (e: Exception) {
                        println(it)
                        throw RuntimeException(e)
                    }
                }

        }
}
