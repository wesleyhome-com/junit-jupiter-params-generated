package com.wesleyhome.test.jupiter.generator

import java.lang.reflect.Method
import java.lang.reflect.Modifier

/** A function that can be called before the test instance exists, and what to call it on. */
internal data class StaticFunction(val method: Method, val receiver: Any?)

/**
 * Finds [name] as a static method, a member of an object, or a member of a companion object. Walks
 * superclasses, so a function shared by several test classes can live in a base class.
 */
internal fun findStaticFunction(type: Class<*>, name: String): StaticFunction? {
    hierarchyOf(type).forEach { candidate ->
        candidate.declaredMethods
            .firstOrNull { it.isNamed(name) && Modifier.isStatic(it.modifiers) }
            ?.let { return StaticFunction(it, null) }
        objectInstanceOf(candidate)?.let { instance ->
            candidate.declaredMethods
                .firstOrNull { it.isNamed(name) }
                ?.let { return StaticFunction(it, instance) }
        }
        companionOf(candidate)?.let { companion ->
            companion.javaClass.declaredMethods
                .firstOrNull { it.isNamed(name) }
                ?.let { return StaticFunction(it, companion) }
        }
    }
    return null
}

/** Whether [name] exists only as an instance method, which nothing can call before the test instance exists. */
internal fun hasInstanceFunction(type: Class<*>, name: String): Boolean =
    hierarchyOf(type).flatMap { it.declaredMethods.asSequence() }.any { it.isNamed(name) }

/** The prefix check keeps Kotlin reflection off every method that could not be a match. */
private fun Method.isNamed(name: String): Boolean =
    this.name.startsWith(name) && kotlinName() == name

private fun hierarchyOf(type: Class<*>): Sequence<Class<*>> =
    generateSequence(type) { it.superclass }.takeWhile { it != Any::class.java }

private fun companionOf(type: Class<*>): Any? =
    runCatching { type.getDeclaredField("Companion").apply { isAccessible = true }.get(null) }.getOrNull()

private fun objectInstanceOf(type: Class<*>): Any? =
    runCatching { type.getDeclaredField("INSTANCE").get(null) }.getOrNull()
