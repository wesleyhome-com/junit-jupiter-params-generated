package com.wesleyhome.test.jupiter.generator

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.full.companionObject

/** A function that can be called before the test instance exists, and what to call it on. */
internal data class StaticFunction(val method: Method, val receiver: Any?)

/**
 * Finds [name] as a static method, a member of an object, or a member of a companion object, in the
 * nearest of [scopesOf] that has it, so a function shared by several test classes can live in a
 * base class, an interface, or the class enclosing a `@Nested` one.
 */
internal fun findStaticFunction(type: Class<*>, name: String): StaticFunction? {
    scopesOf(type).forEach { candidate ->
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
    scopesOf(type).flatMap { it.declaredMethods.asSequence() }.any { it.isNamed(name) }

/** Every method declared where [findStaticFunction] looks, whether or not it could be called there. */
internal fun declaredFunctionsOf(type: Class<*>): Sequence<Method> =
    scopesOf(type).flatMap { scope ->
        scope.declaredMethods.asSequence() + companionOf(scope)?.javaClass?.declaredMethods.orEmpty()
    }

/** The prefix check keeps Kotlin reflection off every method that could not be a match. */
private fun Method.isNamed(name: String): Boolean =
    this.name.startsWith(name) && kotlinName() == name

/**
 * The class, its superclasses, then its interfaces. A `@Nested` class is an inner class, which can't
 * declare a companion object in Kotlin or static methods before Java 16, so the classes enclosing it
 * follow with their own supertypes. They come last, so they never displace a function found earlier.
 */
private fun scopesOf(type: Class<*>): Sequence<Class<*>> =
    generateSequence(type) { inner -> if (Modifier.isStatic(inner.modifiers)) null else inner.declaringClass }
        .flatMap { supertypesOf(it) }
        .distinct()

private fun supertypesOf(type: Class<*>): Sequence<Class<*>> {
    val classes = generateSequence(type) { it.superclass }.takeWhile { it != Any::class.java }
    return classes + classes.flatMap { interfacesOf(it) }
}

private fun interfacesOf(type: Class<*>): Sequence<Class<*>> =
    type.interfaces.asSequence().flatMap { sequenceOf(it) + interfacesOf(it) }

/**
 * A companion object can be named, and the static field holding it is named after it. That field is
 * private when the companion is.
 */
private fun companionOf(type: Class<*>): Any? {
    if (!type.isAnnotationPresent(Metadata::class.java)) return null
    val companion = type.kotlin.companionObject ?: return null
    return type.getDeclaredField(companion.java.simpleName).apply { isAccessible = true }.get(null)
}

private fun objectInstanceOf(type: Class<*>): Any? =
    runCatching { type.getDeclaredField("INSTANCE").get(null) }.getOrNull()
