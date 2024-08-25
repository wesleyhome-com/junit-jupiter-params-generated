package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.generator.ParametersGenerator
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

class GeneratedParametersTestMethodContext(context: ExtensionContext) {
    val generator: ParametersGenerator

    init {
        val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
        val parameters = requiredTestMethod.parameters.filter { it.kind == KParameter.Kind.VALUE }
        generator = ParametersGenerator(
            testModel = TestModel(
                name = requiredTestMethod.name,
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
