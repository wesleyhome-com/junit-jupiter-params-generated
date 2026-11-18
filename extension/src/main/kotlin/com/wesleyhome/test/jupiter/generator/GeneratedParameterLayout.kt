package com.wesleyhome.test.jupiter.generator

import kotlin.reflect.KClass

internal class GeneratedParameterLayout(
    private val names: Array<String>,
    private val types: Array<KClass<*>>,
    private val nullable: BooleanArray,
    private val slotByParameterIndex: IntArray,
    private val declaredNames: Array<String> = names,
    /** Whether the test's class file records its parameter names, which [declaredNames] then are. */
    val recordsDeclaredNames: Boolean = true
) {

    val size: Int get() = names.size

    /** What display names and report entries call the slot: without `-parameters`, Java's `arg0`. */
    fun nameOf(slot: Int): String = names[slot]

    fun typeOf(slot: Int): KClass<*> = types[slot]

    /** Whether this slot can produce a null, from Kotlin nullability or from `@WithNull`. */
    fun isNullable(slot: Int): Boolean = nullable[slot]

    fun slotOfName(name: String?): Int? = names.indexOf(name).takeIf { it >= 0 }

    fun slotOfDeclaredName(name: String): Int? = declaredNames.indexOf(name).takeIf { it >= 0 }

    /** Whether [nameOf] gives Java's `arg<position>`, which is all a test compiled without `-parameters` has. */
    val namesArePositions: Boolean
        get() = slotByParameterIndex.indices.all { index ->
            slotByParameterIndex[index] == NO_SLOT || names[slotByParameterIndex[index]] == "arg$index"
        }

    fun slotOf(parameterIndex: Int): Int =
        if (parameterIndex in slotByParameterIndex.indices) slotByParameterIndex[parameterIndex] else NO_SLOT

    companion object {
        const val NO_SLOT: Int = -1

        fun of(
            parameters: List<GeneratedParameter>,
            parameterCount: Int,
            recordsDeclaredNames: Boolean
        ): GeneratedParameterLayout {
            val slots = IntArray(parameterCount) { NO_SLOT }
            parameters.forEachIndexed { slot, parameter -> slots[parameter.index] = slot }
            return GeneratedParameterLayout(
                Array(parameters.size) { parameters[it].name },
                Array(parameters.size) { parameters[it].type },
                BooleanArray(parameters.size) { parameters[it].isNullable },
                slots,
                Array(parameters.size) { parameters[it].declaredName },
                recordsDeclaredNames
            )
        }
    }
}
