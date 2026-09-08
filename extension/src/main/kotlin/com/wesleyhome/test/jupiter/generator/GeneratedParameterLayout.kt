package com.wesleyhome.test.jupiter.generator

import kotlin.reflect.KClass

internal class GeneratedParameterLayout(
    private val names: Array<String>,
    private val types: Array<KClass<*>>,
    private val nullable: BooleanArray,
    private val slotByParameterIndex: IntArray
) {

    val size: Int get() = names.size

    fun nameOf(slot: Int): String = names[slot]

    fun typeOf(slot: Int): KClass<*> = types[slot]

    /** Whether this slot can produce a null, from Kotlin nullability or from `@WithNull`. */
    fun isNullable(slot: Int): Boolean = nullable[slot]

    fun slotOfName(name: String?): Int? = names.indexOf(name).takeIf { it >= 0 }

    fun slotOf(parameterIndex: Int): Int =
        if (parameterIndex in slotByParameterIndex.indices) slotByParameterIndex[parameterIndex] else NO_SLOT

    companion object {
        const val NO_SLOT: Int = -1

        fun of(parameters: List<GeneratedParameter>, parameterCount: Int): GeneratedParameterLayout {
            val slots = IntArray(parameterCount) { NO_SLOT }
            parameters.forEachIndexed { slot, parameter -> slots[parameter.index] = slot }
            return GeneratedParameterLayout(
                Array(parameters.size) { parameters[it].name },
                Array(parameters.size) { parameters[it].type },
                BooleanArray(parameters.size) { parameters[it].isNullable },
                slots
            )
        }
    }
}
