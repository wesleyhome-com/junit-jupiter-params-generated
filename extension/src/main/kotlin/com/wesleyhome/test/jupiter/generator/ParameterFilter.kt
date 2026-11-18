package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidFilterException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.kotlinFunction

/**
 * One filter bound to the generated slots its parameters name, so a rule declares only the
 * parameters it is about rather than every parameter the test method has.
 */
internal class ParameterFilter(
    private val name: String,
    val method: Method,
    private val receiver: Any?,
    private val slots: IntArray
) {

    private val parameterTypes: Array<Class<*>> = method.parameterTypes

    /** What reports call it: the declared name, whichever name found it. */
    val declaredName: String get() = method.kotlinName()

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

        /** Looks for each of [names] in [classes] only, the first of which is the test class. */
        fun resolve(
            classes: List<Class<*>>,
            names: Collection<String>,
            layout: GeneratedParameterLayout
        ): List<ParameterFilter> = names.map { name -> resolve(classes, name, layout) }

        private fun resolve(
            classes: List<Class<*>>,
            name: String,
            layout: GeneratedParameterLayout
        ): ParameterFilter {
            val (method, receiver) = find(classes, name)
            // Boolean::class.java is the primitive type in Kotlin, so a boxed return needs its own check.
            if (method.returnType != Boolean::class.javaPrimitiveType &&
                method.returnType != Boolean::class.javaObjectType
            ) {
                throw InvalidFilterException(
                    "Filter [$name] must return Boolean, but returns ${method.returnType.simpleName}"
                )
            }
            val binding = FilterBinding(method, layout)
            if (!binding.byName) {
                logger.warning(
                    "Filter [$name] binds to the test's parameters by position, because the class files don't " +
                        "record their names. Compile with -parameters to bind by name."
                )
            }
            val slots = method.parameters.mapIndexed { index, parameter -> slotFor(name, method, index, parameter, binding) }
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
            binding: FilterBinding
        ): Int {
            val declaredAs = binding.names[index]
            val slot = binding.slotOf(index)
                ?: throw InvalidFilterException(
                    "Filter [$name] declares [$declaredAs], which is not a generated parameter of the test"
                )
            val layout = binding.layout
            val generatedType = layout.typeOf(slot)
            val generated = generatedType.javaObjectType
            val declared = parameter.type
            if (!declared.boxed().isAssignableFrom(generated) &&
                !(generatedType.isValue && method.declaresKotlinType(index, generatedType))
            ) {
                throw InvalidFilterException(
                    "Filter [$name] declares [$declaredAs] as ${declared.simpleName}, " +
                        "but the generated parameter is ${generated.simpleName}"
                )
            }
            if (layout.isNullable(slot) && declared.isPrimitive) {
                throw InvalidFilterException(
                    "Filter [$name] declares [$declaredAs] as ${declared.simpleName}, " +
                        "which cannot hold the null this parameter generates"
                )
            }
            return slot
        }

        private val logger: Logger = Logger.getLogger(ParameterFilter::class.java.name)

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

        private fun find(classes: List<Class<*>>, name: String): StaticFunction =
            findStaticFunction(classes, name) ?: throw InvalidFilterException(
                if (hasInstanceFunction(classes, name)) {
                    "Filter [$name] must be static or declared in a companion object, because filtering " +
                        "happens before the test instance exists"
                } else {
                    "No filter named [$name] on ${classes.first().name}"
                }
            )

        private fun Method.declaresKotlinType(index: Int, type: KClass<*>): Boolean =
            kotlinFunction?.let { it.valueParameters[index].type.classifier == type } == true
    }
}

/**
 * How a filter's parameters find their generated slots: the one decision every caller binds through.
 *
 * Declared names bind when the class files record both the filter's and the test's. Otherwise
 * binding is as it was before names were read: Java reflection's names against the test's display
 * names, which without `-parameters` are `arg0`, `arg1` on both sides, pairing parameters by position.
 */
internal class FilterBinding(method: Method, val layout: GeneratedParameterLayout) {

    private val declared: List<String>? = method.declaredParameterNames()?.takeIf { layout.recordsDeclaredNames }

    val byName: Boolean get() = declared != null

    /** What each parameter is bound by, and so what messages call it. */
    val names: List<String> = declared ?: method.parameters.map { it.name }

    fun slotOf(index: Int): Int? =
        if (byName) layout.slotOfDeclaredName(names[index]) else layout.slotOfName(names[index])
}
