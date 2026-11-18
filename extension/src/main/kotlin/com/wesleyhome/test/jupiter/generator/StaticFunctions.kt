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
 *
 * An `internal` function compiles to `name$module`, and only that compiled name used to find it.
 * Within one class, a function the old matching finds still wins over one found by its Kotlin name
 * alone, so an `internal` overload doesn't displace the function chosen before.
 */
internal fun findStaticFunction(type: Class<*>, name: String): StaticFunction? =
    scopesOf(type).firstNotNullOfOrNull { scope ->
        val named = callableIn(scope).filter { it.method.isNamed(name) }.toList()
        named.firstOrNull { it.method.name == name || '$' !in it.method.name } ?: named.firstOrNull()
    }

/** Whether [name] exists only as an instance method, which nothing can call before the test instance exists. */
internal fun hasInstanceFunction(type: Class<*>, name: String): Boolean =
    scopesOf(type).flatMap { it.declaredMethods.asSequence() }.any { it.isNamed(name) }

/**
 * The class, then the class enclosing it for as long as it is an inner class, as `@Nested` requires.
 * An inner class can't declare a companion object in Kotlin, or static methods before Java 16.
 */
internal fun enclosingClassesOf(type: Class<*>): Sequence<Class<*>> =
    generateSequence(type) { inner -> if (Modifier.isStatic(inner.modifiers)) null else inner.declaringClass }

/** Every method declared on [type], its supertypes and their companion objects, callable or not. */
internal fun declaredFunctionsOf(type: Class<*>): Sequence<Method> =
    supertypesOf(type).flatMap { scope ->
        scope.declaredMethods.asSequence() + companionOf(scope)?.javaClass?.declaredMethods.orEmpty()
    }

/** Matches the compiled name, or the declared name when compiling mangled it. */
private fun Method.isNamed(name: String): Boolean =
    this.name == name || (this.name.startsWith(name) && kotlinName() == name)

/** Static methods, then an object's members, then its companion object's: the order a name is looked for. */
private fun callableIn(scope: Class<*>): Sequence<StaticFunction> =
    scope.declaredMethods.asSequence().filter { Modifier.isStatic(it.modifiers) }.map { StaticFunction(it, null) } +
        membersOf(objectInstanceOf(scope)) +
        membersOf(companionOf(scope))

private fun membersOf(instance: Any?): Sequence<StaticFunction> =
    if (instance == null) {
        emptySequence()
    } else {
        instance.javaClass.declaredMethods.asSequence().map { StaticFunction(it, instance) }
    }

/**
 * Each of [enclosingClassesOf] with its superclasses, then its interfaces. Enclosing classes come
 * after the test class's own supertypes, so they never displace a function found there.
 */
private fun scopesOf(type: Class<*>): Sequence<Class<*>> =
    enclosingClassesOf(type).flatMap { supertypesOf(it) }.distinct()

private fun supertypesOf(type: Class<*>): Sequence<Class<*>> {
    val classes = generateSequence(type) { it.superclass }.takeWhile { it != Any::class.java }
    return classes + classes.flatMap { interfacesOf(it) }
}

private fun interfacesOf(type: Class<*>): Sequence<Class<*>> =
    type.interfaces.asSequence().flatMap { sequenceOf(it) + interfacesOf(it) }

/**
 * A Kotlin companion's field is named after the companion, which can be named, and is private when
 * the companion is. A Java class has no companion, but a static field named `Companion` has always
 * been searched as one. Failing to read either means there is no companion to search, not a failed
 * test: this runs on every supertype, third-party ones included.
 */
private fun companionOf(type: Class<*>): Any? = runCatching {
    val field = if (type.isAnnotationPresent(Metadata::class.java)) {
        type.kotlin.companionObject?.simpleName
    } else {
        "Companion"
    }
    field?.let { type.getDeclaredField(it).apply { isAccessible = true }.get(null) }
}.getOrNull()

private fun objectInstanceOf(type: Class<*>): Any? =
    runCatching { type.getDeclaredField("INSTANCE").get(null) }.getOrNull()
