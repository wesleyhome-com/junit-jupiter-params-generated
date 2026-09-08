package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidFilterException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Parameter

/**
 * One filter bound to the generated slots its parameters name, so a rule declares only the
 * parameters it is about rather than every parameter the test method has.
 */
internal class ParameterFilter(
    private val name: String,
    private val method: Method,
    private val receiver: Any?,
    private val slots: IntArray
) {

    fun accepts(values: Array<Any?>): Boolean {
        val arguments = arrayOfNulls<Any?>(slots.size)
        for (i in slots.indices) {
            arguments[i] = values[slots[i]]
        }
        return method.invoke(receiver, *arguments) as? Boolean
            ?: throw InvalidFilterException("Filter [$name] returned null")
    }

    companion object {

        fun resolve(
            testClass: Class<*>,
            names: Collection<String>,
            layout: GeneratedParameterLayout
        ): List<ParameterFilter> = names.map { name -> resolve(testClass, name, layout) }

        private fun resolve(
            testClass: Class<*>,
            name: String,
            layout: GeneratedParameterLayout
        ): ParameterFilter {
            val (method, receiver) = find(testClass, name)
            // Boolean::class.java is the primitive type in Kotlin, so a boxed return needs its own check.
            if (method.returnType != Boolean::class.javaPrimitiveType &&
                method.returnType != Boolean::class.javaObjectType
            ) {
                throw InvalidFilterException(
                    "Filter [$name] must return Boolean, but returns ${method.returnType.simpleName}"
                )
            }
            val slots = method.parameters.map { parameter -> slotFor(name, parameter, layout) }
            method.isAccessible = true
            return ParameterFilter(name, method, receiver, slots.toIntArray())
        }

        /**
         * Types are checked here rather than left to [Method.invoke], which reports a mismatch as a
         * bare "argument type mismatch" and silently widens a generated Int into a Long parameter.
         */
        private fun slotFor(name: String, parameter: Parameter, layout: GeneratedParameterLayout): Int {
            val slot = layout.slotOfName(parameter.name)
                ?: throw InvalidFilterException(
                    "Filter [$name] declares [${parameter.name}], which is not a generated parameter of the test"
                )
            val generated = layout.typeOf(slot).javaObjectType
            val declared = parameter.type
            if (!declared.boxed().isAssignableFrom(generated)) {
                throw InvalidFilterException(
                    "Filter [$name] declares [${parameter.name}] as ${declared.simpleName}, " +
                        "but the generated parameter is ${generated.simpleName}"
                )
            }
            if (layout.isNullable(slot) && declared.isPrimitive) {
                throw InvalidFilterException(
                    "Filter [$name] declares [${parameter.name}] as ${declared.simpleName}, " +
                        "which cannot hold the null this parameter generates"
                )
            }
            return slot
        }

        private fun Class<*>.boxed(): Class<*> = when (this) {
            Boolean::class.javaPrimitiveType -> Boolean::class.javaObjectType
            Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
            Char::class.javaPrimitiveType -> Char::class.javaObjectType
            Short::class.javaPrimitiveType -> Short::class.javaObjectType
            Int::class.javaPrimitiveType -> Int::class.javaObjectType
            Long::class.javaPrimitiveType -> Long::class.javaObjectType
            Float::class.javaPrimitiveType -> Float::class.javaObjectType
            Double::class.javaPrimitiveType -> Double::class.javaObjectType
            else -> this
        }

        private fun find(testClass: Class<*>, name: String): Pair<Method, Any?> {
            hierarchyOf(testClass).forEach { type ->
                type.declaredMethods
                    .firstOrNull { it.name == name && Modifier.isStatic(it.modifiers) }
                    ?.let { return it to null }
                companionOf(type)?.let { companion ->
                    companion.javaClass.declaredMethods
                        .firstOrNull { it.name == name }
                        ?.let { return it to companion }
                }
            }
            val instanceMethod = hierarchyOf(testClass)
                .flatMap { it.declaredMethods.asSequence() }
                .firstOrNull { it.name == name }
            throw InvalidFilterException(
                if (instanceMethod != null) {
                    "Filter [$name] must be static or declared in a companion object, because filtering " +
                        "happens before the test instance exists"
                } else {
                    "No filter named [$name] on ${testClass.name}"
                }
            )
        }

        /** Walks superclasses so a rule shared by several test classes can live in a base class. */
        private fun hierarchyOf(testClass: Class<*>): Sequence<Class<*>> =
            generateSequence(testClass) { it.superclass }.takeWhile { it != Any::class.java }

        private fun companionOf(testClass: Class<*>): Any? =
            runCatching { testClass.getDeclaredField("Companion").apply { isAccessible = true }.get(null) }.getOrNull()
    }
}
