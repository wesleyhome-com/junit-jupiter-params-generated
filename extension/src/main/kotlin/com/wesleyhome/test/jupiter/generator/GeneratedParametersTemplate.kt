package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.api.extension.ParameterContext
import java.lang.reflect.Method

internal class GeneratedParametersTemplate(
    private val testMethod: Method,
    val formatter: InvocationDisplayNameFormatter,
    private val layout: GeneratedParameterLayout,
    val reportValues: Boolean = false
) {

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

    fun valuesByName(values: Array<Any?>): Map<String, String> =
        (0 until layout.size).associate { slot ->
            "generated.${layout.nameOf(slot)}" to values[slot].toString()
        }
}
