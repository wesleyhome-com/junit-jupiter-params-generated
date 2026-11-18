package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolutionException
import java.lang.reflect.Method

internal class GeneratedParametersTemplate(
    private val testMethod: Method,
    val formatter: InvocationDisplayNameFormatter,
    private val layout: GeneratedParameterLayout,
    val reportValues: Boolean = false
) {

    /**
     * The value classes each of the test method's parameters takes unboxed, as its Kotlin type says.
     * Null when the Kotlin parameters don't line up with the compiled ones, as for a Java test or a
     * suspending one.
     */
    private val unboxing: Array<Array<ValueClassShape>>? = testMethod.kotlinValueParameters()?.let { parameters ->
        Array(parameters.size) { index -> unboxedLayers(parameters[index].type, testMethod.parameterTypes[index]) }
    }

    /**
     * A resolver is consulted for constructor and lifecycle-method parameters too, so matching on
     * index alone would claim a constructor parameter that happens to sit at a generated position.
     */
    fun slotOf(parameterContext: ParameterContext): Int =
        if (parameterContext.declaringExecutable == testMethod) {
            layout.slotOf(parameterContext.index)
        } else {
            GeneratedParameterLayout.NO_SLOT
        }

    fun resolve(parameterContext: ParameterContext, values: Array<Any?>): Any? {
        val slot = slotOf(parameterContext)
        val value = values[slot]
        if (value is RejectedValue) {
            throw ParameterResolutionException(
                "Generated value [${value.raw}] for parameter [${layout.nameOf(slot)}] was rejected by " +
                    "${value.valueClass.simpleName}: ${value.cause.javaClass.simpleName}: ${value.cause.message}",
                value.cause
            )
        }
        val unboxing = unboxing ?: return value.adaptedTo(parameterContext.parameter.type)
        return value.unboxedFrom(unboxing[parameterContext.index])
    }

    /**
     * The test method's [arguments] as its compiled signature takes them, for calling it with
     * `Method.invoke` as JUnit 6.0 does, or null to leave the call to JUnit. JUnit 6.1 calls it
     * through kotlin-reflect when an argument is a value class's box, and kotlin-reflect takes every
     * value-class argument to be boxed, which the `long` a `Money` compiles to isn't. Without a box
     * among them, both versions make the call this would. [resolve] has already compiled each
     * generated value. Another resolver's box is unboxed, as kotlin-reflect does.
     */
    fun compiledArguments(arguments: List<Any?>): Array<Any?>? {
        val unboxing = unboxing ?: return null
        if (arguments.none { it != null && it.javaClass.isAnnotationPresent(JvmInline::class.java) }) return null
        return Array(arguments.size) { index ->
            if (layout.slotOf(index) == GeneratedParameterLayout.NO_SLOT) {
                arguments[index].unboxedFrom(unboxing[index])
            } else {
                arguments[index]
            }
        }
    }

    fun valuesByName(values: Array<Any?>): Map<String, String> =
        (0 until layout.size).associate { slot ->
            "generated.${layout.nameOf(slot)}" to values[slot].toString()
        }
}
