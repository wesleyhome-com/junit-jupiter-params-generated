package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

internal class GeneratedParametersTestMethodContext(context: ExtensionContext) {
    val generator: ParametersGenerator

    init {
        val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
        val parameters = requiredTestMethod.parameters.filter { it.kind == KParameter.Kind.VALUE }
        generator = ParametersGenerator(
            testModel = TestModel(
                testParameters = parameters.map {
                    TestParameter(
                        name = it.name!!,
                        type = it.type.classifier as KClass<*>,
                        isNullable = it.type.isMarkedNullable,
                        annotations = it.annotations.toList()
                    )
                }
            )
        )
    }
}
