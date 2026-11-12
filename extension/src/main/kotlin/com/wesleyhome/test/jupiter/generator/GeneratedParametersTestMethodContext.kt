package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.annotations.DEFAULT_DISPLAY_NAME
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.extension.ExtensionContext
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.kotlinFunction

internal class GeneratedParametersTestMethodContext(context: ExtensionContext) {
    val testModel: TestModel
    val namePattern: String
    val filterNames: Set<String>
    val strategy: GenerationStrategy

    init {
        val requiredTestMethod = context.requiredTestMethod.kotlinFunction!!
        val generated = requiredTestMethod.findAnnotation<GeneratedParametersTest>()
        namePattern = generated?.name ?: DEFAULT_DISPLAY_NAME
        filterNames = conventionalFilterNames(requiredTestMethod.name, context.requiredTestMethod.declaringClass) +
            (generated?.filters?.toSet() ?: emptySet())
        strategy = generated?.strategy ?: GenerationStrategy.CARTESIAN
        val parameters = requiredTestMethod.parameters.filter { it.kind == KParameter.Kind.VALUE }
        val jvmTypes = context.requiredTestMethod.parameterTypes
        testModel = TestModel(
            testParameters = parameters.mapIndexed { index, parameter ->
                TestParameter(
                    // Java parameter names need -parameters at compile time; fall back to the
                    // position so a display name stays readable without it.
                    name = parameter.name ?: "arg$index",
                    type = parameter.type.classifier as KClass<*>,
                    isNullable = generatesNull(parameter, jvmTypes[index]),
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
    private fun conventionalFilterNames(testName: String, testClass: Class<*>): Set<String> {
        val prefix = "$testName$FILTER_SUFFIX"
        return generateSequence(testClass) { it.superclass }
            .takeWhile { it != Any::class.java }
            .flatMap { type -> type.declaredMethods.asSequence() + companionMethods(type) }
            .filter { it.name.startsWith(prefix) }
            .map { it.kotlinName() }
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
     *
     * The compiled signature decides what can hold null, not the Kotlin type: a non-null value class
     * over Long is a `long` there.
     */
    private fun generatesNull(parameter: KParameter, jvmType: Class<*>): Boolean {
        val withNull = parameter.annotations.any { it is WithNull }
        if (withNull) {
            require(!jvmType.isPrimitive) {
                "@WithNull cannot be applied to [${parameter.name}]: ${jvmType.typeName} cannot hold null"
            }
        }
        return withNull || parameter.type.isMarkedNullable
    }

    companion object {
        const val FILTER_SUFFIX: String = "_filter"
    }
}
