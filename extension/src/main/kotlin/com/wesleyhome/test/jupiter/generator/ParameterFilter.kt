package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidFilterException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

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

    private val parameterTypes: Array<Class<*>> = method.parameterTypes

    fun accepts(values: Array<Any?>): Boolean {
        val arguments = arrayOfNulls<Any?>(slots.size)
        for (i in slots.indices) {
            val value = values[slots[i]]
            // Filtering this combination out would hide the rejection; letting it run reports it.
            if (value is RejectedValue) return true
            arguments[i] = value.adaptedTo(parameterTypes[i])
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
            val slots = method.parameters.mapIndexed { index, parameter -> slotFor(name, method, index, parameter, layout) }
            method.isAccessible = true
            return ParameterFilter(name, method, receiver, slots.toIntArray())
        }

        /**
         * Types are checked here rather than left to [Method.invoke], which reports a mismatch as a
         * bare "argument type mismatch" and silently widens a generated Int into a Long parameter.
         *
         * A value class compiles to its underlying type, so `amount: Money` and `amount: Long` are both
         * a `long` in the signature; only the Kotlin declaration tells them apart.
         */
        private fun slotFor(
            name: String,
            method: Method,
            index: Int,
            parameter: Parameter,
            layout: GeneratedParameterLayout
        ): Int {
            val slot = layout.slotOfName(parameter.name)
                ?: throw InvalidFilterException(
                    "Filter [$name] declares [${parameter.name}], which is not a generated parameter of the test"
                )
            val generatedType = layout.typeOf(slot)
            val generated = generatedType.javaObjectType
            val declared = parameter.type
            if (!declared.boxed().isAssignableFrom(generated) &&
                !(generatedType.isValue && method.declaresKotlinType(index, generatedType))
            ) {
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

        private fun find(testClass: Class<*>, name: String): StaticFunction =
            findStaticFunction(testClass, name) ?: throw InvalidFilterException(
                if (hasInstanceFunction(testClass, name)) {
                    "Filter [$name] must be static or declared in a companion object, because filtering " +
                        "happens before the test instance exists"
                } else {
                    "No filter named [$name] on ${testClass.name}"
                }
            )

        private fun Method.declaresKotlinType(index: Int, type: KClass<*>): Boolean =
            kotlinFunction?.let { it.valueParameters[index].type.classifier == type } == true
    }
}
