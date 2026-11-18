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
    private val slots: IntArray,
    private val boundByPositionBecause: String?
) {

    private val parameterTypes: Array<Class<*>> = method.parameterTypes

    /** What reports call it: the declared name, whichever name found it. */
    val declaredName: String get() = method.kotlinName()

    /** Called once for each filter a test uses, after a filter reached by two names has become one. */
    fun warnIfBoundByPosition(test: String) {
        boundByPositionBecause?.let { logger.warning("Filter [$name] binds to the parameters of [$test] by position, because $it") }
    }

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
        ): ParameterFilter = choose(name, find(classes, name), layout).also { it.method.isAccessible = true }

        /**
         * 4.1.0 used the first overload in the first of [ranks], and within a rank that was whichever
         * reflection listed first, which changes between builds. An overload that can't filter the test
         * could only have failed it, so the first rank holding one that can decides. If that rank holds
         * several, which of them applied was never settled, so the test fails rather than pick one.
         */
        internal fun choose(
            name: String,
            ranks: List<List<StaticFunction>>,
            layout: GeneratedParameterLayout
        ): ParameterFilter {
            ranks.flatten().singleOrNull()?.let { return bind(name, it, layout) }
            val problems = mutableListOf<String>()
            for (rank in ranks) {
                val fitting = rank.mapNotNull { overload ->
                    try {
                        bind(name, overload, layout)
                    } catch (e: InvalidFilterException) {
                        problems += "${signatureOf(name, overload.method)} ${e.message!!.removePrefix("Filter [$name] ")}"
                        null
                    }
                }
                if (fitting.size > 1) {
                    throw InvalidFilterException(
                        "Filter [$name] is overloaded, and ${fitting.size} overloads can filter this test: " +
                            fitting.map { signatureOf(name, it.method) }.sorted().joinToString(", ") +
                            ". Give them different names."
                    )
                }
                fitting.singleOrNull()?.let { return it }
            }
            throw InvalidFilterException(
                "Filter [$name] is overloaded, and no overload can filter this test: " +
                    problems.sorted().joinToString("; ")
            )
        }

        private fun bind(name: String, function: StaticFunction, layout: GeneratedParameterLayout): ParameterFilter {
            val (method, receiver) = function
            // Boolean::class.java is the primitive type in Kotlin, so a boxed return needs its own check.
            if (method.returnType != Boolean::class.javaPrimitiveType &&
                method.returnType != Boolean::class.javaObjectType
            ) {
                throw InvalidFilterException(
                    "Filter [$name] must return Boolean, but returns ${method.returnType.simpleName}"
                )
            }
            val binding = FilterBinding(method, layout)
            val slots = method.parameters.mapIndexed { index, parameter -> slotFor(name, method, index, parameter, binding) }
            return ParameterFilter(name, method, receiver, slots.toIntArray(), binding.boundByPositionBecause)
        }

        private fun signatureOf(name: String, method: Method): String =
            method.parameterTypes.joinToString(", ", "$name(", ")") { it.simpleName }

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

        private fun find(classes: List<Class<*>>, name: String): List<List<StaticFunction>> =
            findOverloads(classes, name).ifEmpty {
                throw InvalidFilterException(
                    if (hasInstanceFunction(classes, name)) {
                        "Filter [$name] must be static or declared in a companion object, because filtering " +
                            "happens before the test instance exists"
                    } else {
                        "No filter named [$name] on ${classes.first().name}"
                    }
                )
            }

        private fun Method.declaresKotlinType(index: Int, type: KClass<*>): Boolean =
            kotlinFunction?.let { it.valueParameters[index].type.classifier == type } == true
    }
}

/**
 * How a filter's parameters find their generated slots: the one decision every caller binds through.
 *
 * 4.1.0 matched Java reflection's names against the test's display names. For Java compiled without
 * `-parameters`, both are `arg0`, `arg1`, so parameters paired up by position. Where they did,
 * position still decides, unless a declared name matches a generated parameter at another position:
 * that pairing was wrong, so the declared names decide. Elsewhere, declared names bind when the class
 * files record both sides' names, and otherwise 4.1.0's matching does.
 *
 * A bridge for a generic override has the overriding method's names in its debug information, but
 * only the overriding method should bind by them.
 */
internal class FilterBinding(method: Method, val layout: GeneratedParameterLayout) {

    private val compiled: List<String> = method.parameters.map { it.name }

    private val recorded: List<String>? = method.takeUnless { it.isBridge }?.declaredParameterNames()

    private val declared: List<String>? = recorded?.takeIf { layout.recordsDeclaredNames }

    private val pairedByPosition: Boolean =
        compiled.isNotEmpty() && compiled.indices.all { compiled[it] == "arg$it" } && layout.namesArePositions

    private val bindingNames: List<String>? = declared?.takeIf { !pairedByPosition || contradictsPositions(it) }

    /** What messages call each parameter: its declared name wherever one is recorded, whatever binds it. */
    val names: List<String> = recorded ?: compiled

    fun slotOf(index: Int): Int? =
        if (bindingNames != null) layout.slotOfDeclaredName(bindingNames[index]) else layout.slotOfName(compiled[index])

    /** Why the filter binds by position, when names could have bound it otherwise. */
    val boundByPositionBecause: String? = when {
        !pairedByPosition || bindingNames != null -> null
        declared == null -> "the class files don't record their names. Compile with -parameters to bind by name."
        else -> declared.filter { layout.slotOfDeclaredName(it) == null }.takeIf { it.isNotEmpty() }?.let {
            "the test has no parameter named $it. Name the filter's parameters as the test does to bind by name."
        }
    }

    private fun contradictsPositions(declared: List<String>): Boolean = declared.indices.any { index ->
        layout.slotOfDeclaredName(declared[index]).let { it != null && it != layout.slotOfName(compiled[index]) }
    }
}
