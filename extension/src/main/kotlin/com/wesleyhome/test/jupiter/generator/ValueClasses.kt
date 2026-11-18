package com.wesleyhome.test.jupiter.generator

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.kotlinFunction

/**
 * A Kotlin value class, as the JVM sees it: a box around one underlying value, which a function
 * signature takes unboxed unless the parameter is nullable or generic.
 */
internal class ValueClassShape private constructor(private val type: KClass<*>) {

    private val constructor = type.primaryConstructor!!.apply { isAccessible = true }

    /** The wrapped value's declared type, `String?` for `Nickname(val value: String?)`. */
    val underlyingType: KType = constructor.parameters.single().type

    val underlying: KClass<*> = underlyingType.classifier as? KClass<*> ?: Any::class

    val boxClass: Class<*> = type.java

    private val property = type.declaredMemberProperties
        .single { it.name == constructor.parameters.single().name }
        .apply { isAccessible = true }

    /** Runs the value class's `init`, so a value it rejects comes back as a [RejectedValue]. */
    fun wrap(raw: Any): Any =
        try {
            constructor.call(raw)
        } catch (e: InvocationTargetException) {
            RejectedValue(type, raw, e.targetException)
        }

    fun unbox(box: Any): Any? = property.getter.call(box)

    companion object {
        private val shapes = ConcurrentHashMap<KClass<*>, ValueClassShape>()

        fun of(type: KClass<*>): ValueClassShape? =
            if (type.isValue) shapes.computeIfAbsent(type, ::ValueClassShape) else null

        /** A type parameter has no shape of its own, even when bounded by a value class. */
        fun of(type: KType): ValueClassShape? = (type.classifier as? KClass<*>)?.let(::of)
    }
}

/**
 * The value classes that a parameter of Kotlin [type] takes unboxed, outermost first, when it
 * compiles to [compiled]. The compiled type alone can't say: `AnyBox(val value: Any)` compiles to
 * `Object`, and the box is an `Object` too. Unboxing stops at a layer the signature takes boxed, as
 * `Money?` compiles to `Money`, and at a type parameter: a generic box already holds what its
 * signature takes.
 */
internal fun unboxedLayers(type: KType, compiled: Class<*>): Array<ValueClassShape> =
    generateSequence(ValueClassShape.of(type)) { ValueClassShape.of(it.underlyingType) }
        .takeWhile { it.boxClass != compiled }
        .toList()
        .toTypedArray()

/** Unboxes [this] from each of [layers] in turn, for as long as it is that layer's box. */
internal fun Any?.unboxedFrom(layers: Array<ValueClassShape>): Any? {
    var current = this
    for (layer in layers) {
        if (!layer.boxClass.isInstance(current)) break
        current = layer.unbox(current!!)
    }
    return current
}

/**
 * A generated value the value class's `init` refused. It takes its place among the options rather
 * than being dropped, so the invocation that would have received it fails and says why.
 */
internal class RejectedValue(val valueClass: KClass<*>, val raw: Any, val cause: Throwable) {
    override fun toString(): String = "${valueClass.simpleName}($raw) rejected"
}

/**
 * Unboxes value classes until [declared] can hold the result, for a test method whose Kotlin
 * parameters don't line up with its compiled ones, so can't say how far to unbox.
 */
internal fun Any?.adaptedTo(declared: Class<*>): Any? {
    var current = this
    while (current != null && !declared.kotlin.javaObjectType.isInstance(current)) {
        current = (ValueClassShape.of(current::class) ?: return current).unbox(current)
    }
    return current
}

/**
 * The name the function was declared with. A function taking a value class compiles to a mangled
 * name such as `test-abc12`, and an `internal` one to `test$<module>`. A backticked name may contain
 * a hyphen of its own, so the suffix cannot simply be cut off.
 */
internal fun Method.kotlinName(): String =
    if ('-' in name || '$' in name) kotlinFunction?.name ?: name else name
