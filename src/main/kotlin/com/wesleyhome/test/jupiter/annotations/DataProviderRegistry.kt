package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import io.github.classgraph.ClassGraph

object DataProviderRegistry {
    private val allDataProviders by lazy {
        ClassGraph()
            .enableAnnotationInfo()
            .enableClassInfo()
            .scan().use { scanResult ->
                scanResult.allClasses.filterNot { it.isAbstract }
                    .filter { it.implementsInterface(ParameterDataProvider::class.java) }
                    .groupBy { it.hasAnnotation(DefaultProvider::class.java) }
                    .mapValues { (_, it) ->
                        it.map { classInfo ->
                            try {
                                classInfo.loadClass().getConstructor().newInstance() as ParameterDataProvider<*>
                            } catch (e: Exception) {
                                throw RuntimeException(e)
                            }
                        }
                    }
            }
    }
    val dataProviders by lazy { allDataProviders.getValue(false) }
    val defaultDataProviders by lazy {
        allDataProviders.getValue(true)
            .groupBy { it.dataProviderFor() }
            .mapValues { (_, it) -> it.toSortedSet(compareBy { it.javaClass.getAnnotation(DefaultProvider::class.java).priority }) }
            .values.flatten()
    }
}
