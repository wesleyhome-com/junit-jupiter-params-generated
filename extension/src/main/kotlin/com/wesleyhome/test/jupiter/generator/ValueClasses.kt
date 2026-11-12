package com.wesleyhome.test.jupiter.generator

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
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

    val underlying: KClass<*> = constructor.parameters.single().type.classifier as? KClass<*> ?: Any::class

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
    }
}

/**
 * A generated value the value class's `init` refused. It takes its place among the options rather
 * than being dropped, so the invocation that would have received it fails and says why.
 */
internal class RejectedValue(val valueClass: KClass<*>, val raw: Any, val cause: Throwable) {
    override fun toString(): String = "${valueClass.simpleName}($raw) rejected"
}

/**
 * Unboxes value classes until [declared] can hold the result. Generated values are kept boxed, so
 * this is where a `Money` becomes the `long` the compiled method takes.
 */
internal fun Any?.adaptedTo(declared: Class<*>): Any? {
    var current = this
    while (current != null && !declared.kotlin.javaObjectType.isInstance(current)) {
        current = ValueClassShape.of(current::class)?.unbox(current) ?: return current
    }
    return current
}

/**
 * The name the function was declared with. A function taking a value class compiles to a mangled
 * name such as `test-abc12`, and a backticked name may contain a hyphen of its own, so the suffix
 * cannot simply be cut off.
 */
internal fun Method.kotlinName(): String = if ('-' in name) kotlinFunction?.name ?: name else name
