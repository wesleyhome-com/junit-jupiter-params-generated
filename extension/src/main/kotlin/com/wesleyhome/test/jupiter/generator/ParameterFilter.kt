package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidFilterException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
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
    private val boundByPositionBecause: String?,
    private val layout: GeneratedParameterLayout,
    /** The message for each slot generating a null that the filter's Kotlin parameter type can't hold. */
    private val refusedNulls: Map<Int, String>,
    /** The value classes each argument is unboxed from, as its Kotlin parameter type says. */
    private val unboxing: Array<Array<ValueClassShape>>
) {

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
            arguments[i] = value.unboxedFrom(unboxing[i])
        }
        val accepted = try {
            method.invoke(receiver, *arguments)
        } catch (e: InvocationTargetException) {
            throw threw(e.targetException, values)
        }
        return accepted as? Boolean ?: throw InvalidFilterException("Filter [$name] returned null")
    }

    /** JUnit ends the whole run on an OutOfMemoryError; wrapping one would fail this test and carry on. */
    private fun threw(cause: Throwable, values: Array<Any?>): Throwable =
        if (cause is OutOfMemoryError) {
            cause
        } else {
            InvalidFilterException(
                "Filter [$name] on [${InvocationDisplayNameFormatter.argumentsWithNames(values, layout)}] " +
                    "threw ${cause.javaClass.simpleName}: ${cause.message}",
                cause
            )
        }

    companion object {

        /**
         * Rejects a filter whose Kotlin parameter can't hold the null its slot generates, as [slotFor]
         * rejects a primitive one, unless another of the test's filters takes that parameter too.
         * Conventional filters run in no fixed order, so that one may run first and turn away every
         * combination holding the null. Without one, whatever combinations reach the filter, the same
         * ones holding the null reach it as well.
         */
        fun requireNullsHeld(filters: List<ParameterFilter>) {
            for (filter in filters) {
                filter.refusedNulls.entries
                    .firstOrNull { (slot, _) -> filters.none { other -> other !== filter && slot in other.slots } }
                    ?.let { throw InvalidFilterException(it.value) }
            }
        }

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
         *
         * An overload that bound by position, but whose declared names say otherwise and then don't
         * all match, still counts as one that can: it fails the test, as it would alone, rather than
         * quietly giving way to another overload.
         */
        internal fun choose(
            name: String,
            ranks: List<List<StaticFunction>>,
            layout: GeneratedParameterLayout
        ): ParameterFilter {
            ranks.flatten().singleOrNull()?.let { return bind(name, it, layout) }
            val problems = mutableListOf<String>()
            for (rank in ranks) {
                val fitting = mutableListOf<ParameterFilter>()
                val failingByName = mutableListOf<Pair<Method, InvalidFilterException>>()
                for (overload in rank) {
                    try {
                        fitting += bind(name, overload, layout)
                    } catch (e: InvalidFilterException) {
                        if (boundByReflection(name, overload, layout)) {
                            failingByName += overload.method to e
                        } else {
                            problems += "${signatureOf(name, overload.method)} ${e.message!!.removePrefix("Filter [$name] ")}"
                        }
                    }
                }
                val candidates = fitting.map { it.method } + failingByName.map { it.first }
                if (candidates.size > 1) {
                    throw InvalidFilterException(
                        "Filter [$name] is overloaded, and ${candidates.size} overloads can filter this test: " +
                            candidates.map { signatureOf(name, it) }.sorted().joinToString(", ") +
                            ". Give them different names."
                    )
                }
                failingByName.singleOrNull()?.let { throw it.second }
                fitting.singleOrNull()?.let { return it }
            }
            throw InvalidFilterException(
                "Filter [$name] is overloaded, and no overload can filter this test: " +
                    problems.sorted().joinToString("; ")
            )
        }

        /** Whether 4.1.0's matching binds [overload], with no declared name able to change it. */
        private fun boundByReflection(name: String, overload: StaticFunction, layout: GeneratedParameterLayout): Boolean =
            runCatching { bind(name, overload, layout, namesMayDecide = false) }.isSuccess

        private fun bind(
            name: String,
            function: StaticFunction,
            layout: GeneratedParameterLayout,
            namesMayDecide: Boolean = true
        ): ParameterFilter {
            val (method, receiver) = function
            // Boolean::class.java is the primitive type in Kotlin, so a boxed return needs its own check.
            if (method.returnType != Boolean::class.javaPrimitiveType &&
                method.returnType != Boolean::class.javaObjectType
            ) {
                throw InvalidFilterException(
                    "Filter [$name] must return Boolean, but returns ${method.returnType.simpleName}"
                )
            }
            val binding = FilterBinding(method, layout, namesMayDecide)
            val kotlinTypes = method.kotlinValueParameters()?.map { it.type }
            val slots = method.parameters.mapIndexed { index, parameter ->
                slotFor(name, kotlinTypes?.get(index), index, parameter, binding)
            }
            return ParameterFilter(
                name,
                method,
                receiver,
                slots.toIntArray(),
                binding.boundByPositionBecause,
                layout,
                refusedNulls(name, method, slots, binding),
                unboxing(method, kotlinTypes)
            )
        }

        /** Without Kotlin types, as for a Java filter, the boxes go as they are: a value class is a class to Java. */
        private fun unboxing(method: Method, kotlinTypes: List<KType>?): Array<Array<ValueClassShape>> =
            Array(method.parameterCount) { index ->
                kotlinTypes?.let { unboxedLayers(it[index], method.parameterTypes[index]) } ?: emptyArray()
            }

        private fun signatureOf(name: String, method: Method): String =
            method.parameterTypes.joinToString(", ", "$name(", ")") { it.simpleName }

        /**
         * Only where Kotlin's own null check would fail the test anyway. Kotlin compiles that check into
         * every function but a private one, so a private filter takes the null, as a Java filter does.
         * A type parameter isn't judged: an unbounded one takes the null, and one bounded by `Any` fails
         * on it when it's reached, reported as the filter throwing.
         */
        private fun refusedNulls(
            name: String,
            method: Method,
            slots: List<Int>,
            binding: FilterBinding
        ): Map<Int, String> {
            if (method.kotlinFunction?.visibility == KVisibility.PRIVATE) return emptyMap()
            val kotlinParameters = method.kotlinValueParameters() ?: return emptyMap()
            return slots.withIndex().mapNotNull { (index, slot) ->
                val type = kotlinParameters[index].type
                if (binding.layout.isNullable(slot) && !type.takesNull()) {
                    slot to cannotHoldNull(name, binding.names[index], (type.classifier as KClass<*>).simpleName)
                } else {
                    null
                }
            }.toMap()
        }

        /**
         * Whether a JVM null is a value of [this] type. A value class's JVM type doesn't say: `Name` over
         * `String` compiles to a `String` that rejects null, but `Nickname` over `String?` compiles to one
         * where null is `Nickname(null)`, the value a test declaring `Nickname` receives too.
         */
        private fun KType.takesNull(): Boolean =
            isMarkedNullable || when (val type = classifier) {
                is KClass<*> -> ValueClassShape.of(type)?.underlyingType?.takesNull() == true
                else -> true
            }

        private fun cannotHoldNull(name: String, declaredAs: String, type: String?): String =
            "Filter [$name] declares [$declaredAs] as $type, which cannot hold the null this parameter generates"

        /**
         * Types are checked here rather than left to [Method.invoke], which reports a mismatch as a
         * bare "argument type mismatch" and silently widens a generated Int into a Long parameter.
         *
         * A value class compiles to its underlying type, so `amount: Money` and `amount: Long` are both
         * a `long` in the signature; only the Kotlin declaration tells them apart.
         */
        private fun slotFor(
            name: String,
            kotlinType: KType?,
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
                !(generatedType.isValue && kotlinType?.classifier == generatedType)
            ) {
                throw InvalidFilterException(
                    "Filter [$name] declares [$declaredAs] as ${declared.simpleName}, " +
                        "but the generated parameter is ${generated.simpleName}"
                )
            }
            if (layout.isNullable(slot) && declared.isPrimitive) {
                throw InvalidFilterException(cannotHoldNull(name, declaredAs, declared.simpleName))
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
    }
}

/**
 * How a filter's parameters find their generated slots: the one decision every caller binds through.
 *
 * 4.1.0 matched Java reflection's names against the test's display names. That matching stands
 * unless a declared name says a parameter belongs to a generated parameter it didn't give it, either
 * another one or, where reflection's names don't match, any at all. The declared names then decide.
 * For Java compiled without `-parameters`, reflection's names are `arg0`, `arg1` on both sides, so
 * that matching pairs parameters by position, and a filter that repeats the test's parameters in
 * order keeps them whatever it calls them.
 * With [namesMayDecide] off, that matching is all there is, as in 4.1.0.
 *
 * A bridge for a generic override has the overriding method's names in its debug information, but
 * only the overriding method should bind by them.
 */
internal class FilterBinding(method: Method, val layout: GeneratedParameterLayout, namesMayDecide: Boolean = true) {

    private val compiled: List<String> = method.parameters.map { it.name }

    private val recorded: List<String>? = method.takeUnless { it.isBridge }?.declaredParameterNames()

    private val declared: List<String>? = recorded?.takeIf { layout.recordsDeclaredNames }

    private val pairedByPosition: Boolean =
        compiled.isNotEmpty() && compiled.indices.all { compiled[it] == "arg$it" } && layout.namesArePositions

    private val bindingNames: List<String>? = declared?.takeIf { namesMayDecide && contradictsReflection(it) }

    /** What messages call each parameter: its declared name wherever one is recorded, whatever binds it. */
    val names: List<String> = recorded ?: compiled

    fun slotOf(index: Int): Int? =
        if (bindingNames != null) layout.slotOfDeclaredName(bindingNames[index]) else layout.slotOfName(compiled[index])

    /** Why the filter binds by position, when names could have bound it otherwise. */
    val boundByPositionBecause: String? = when {
        bindingNames != null || !pairedByPosition -> null
        declared == null -> "the class files don't record their names. Compile with -parameters to bind by name."
        else -> declared.filter { layout.slotOfDeclaredName(it) == null }.takeIf { it.isNotEmpty() }?.let {
            "the test has no parameter named $it. Name the filter's parameters as the test does to bind by name."
        }
    }

    private fun contradictsReflection(declared: List<String>): Boolean = declared.indices.any { index ->
        layout.slotOfDeclaredName(declared[index]).let { it != null && it != layout.slotOfName(compiled[index]) }
    }
}
