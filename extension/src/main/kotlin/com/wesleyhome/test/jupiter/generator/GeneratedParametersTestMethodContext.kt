package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.annotations.DEFAULT_DISPLAY_NAME
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaType
import kotlin.reflect.jvm.kotlinFunction

internal class GeneratedParametersTestMethodContext(context: ExtensionContext) {
    val testModel: TestModel
    val namePattern: String

    init {
        val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
        namePattern = requiredTestMethod.findAnnotation<GeneratedParametersTest>()?.name ?: DEFAULT_DISPLAY_NAME
        val parameters = requiredTestMethod.parameters.filter { it.kind == KParameter.Kind.VALUE }
        testModel = TestModel(
            testParameters = parameters.mapIndexed { index, parameter ->
                TestParameter(
                    // Java parameter names need -parameters at compile time; fall back to the
                    // position so a display name stays readable without it.
                    name = parameter.name ?: "arg$index",
                    type = parameter.type.classifier as KClass<*>,
                    isNullable = generatesNull(parameter),
                    annotations = parameter.annotations.toList()
                )
            }
        )
    }

    /**
     * A Kotlin nullable type still implies a null case, so tests written before [WithNull] keep
     * working. The annotation says it explicitly and is the only form Java can express, since
     * Kotlin reflection reports every Java type as platform-typed and never as nullable.
     */
    private fun generatesNull(parameter: KParameter): Boolean {
        val withNull = parameter.annotations.any { it is WithNull }
        if (withNull) {
            val javaType = parameter.type.javaType
            require(!(javaType is Class<*> && javaType.isPrimitive)) {
                "@WithNull cannot be applied to [${parameter.name}]: ${javaType.typeName} cannot hold null"
            }
        }
        return withNull || parameter.type.isMarkedNullable
    }
}
