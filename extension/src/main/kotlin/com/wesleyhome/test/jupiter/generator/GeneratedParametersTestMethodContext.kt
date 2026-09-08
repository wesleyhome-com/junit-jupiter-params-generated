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
    val filterNames: Set<String>

    init {
        val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
        val generated = requiredTestMethod.findAnnotation<GeneratedParametersTest>()
        namePattern = generated?.name ?: DEFAULT_DISPLAY_NAME
        filterNames = conventionalFilterNames(context.requiredTestMethod) +
            (generated?.filters?.toSet() ?: emptySet())
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
     * Filters named `<test>_filter` and `<test>_filter_<something>` apply to their test without
     * being listed, so the common case needs no attribute. They are unioned with any named
     * explicitly rather than overridden by them, so adding one can never silently do nothing.
     */
    private fun conventionalFilterNames(testMethod: java.lang.reflect.Method): Set<String> {
        val prefix = "${testMethod.name}$FILTER_SUFFIX"
        return generateSequence(testMethod.declaringClass) { it.superclass }
            .takeWhile { it != Any::class.java }
            .flatMap { type -> type.declaredMethods.asSequence() + companionMethods(type) }
            .map { it.name }
            .filter { it == prefix || it.startsWith("${prefix}_") }
            .toSet()
    }

    private fun companionMethods(type: Class<*>): Sequence<java.lang.reflect.Method> =
        runCatching { type.getDeclaredField("Companion").apply { isAccessible = true }.get(null) }
            .getOrNull()
            ?.javaClass
            ?.declaredMethods
            ?.asSequence()
            ?: emptySequence()

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

    companion object {
        const val FILTER_SUFFIX: String = "_filter"
    }
}
