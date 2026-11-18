package com.wesleyhome.test.jupiter.generator

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.full.companionObject

/** A function that can be called before the test instance exists, and what to call it on. */
internal data class StaticFunction(val method: Method, val receiver: Any?)

/**
 * Finds [name] as static methods, members of an object, or members of a companion object, in
 * [type] and its supertypes, then in the classes enclosing it if it's `@Nested`. A function shared
 * by several test classes can live in a base class, an interface, or the class enclosing a
 * `@Nested` one.
 */
internal fun findOverloads(type: Class<*>, name: String): List<List<StaticFunction>> =
    findOverloads(enclosingClassesOf(type).toList(), name)

/**
 * Looks in [classes] only, each with its supertypes, and returns the functions named [name] in the
 * nearest that has any, ranked by [rankOverloads].
 */
internal fun findOverloads(classes: List<Class<*>>, name: String): List<List<StaticFunction>> =
    rankOverloads(
        name,
        scopesOf(classes)
            .map { scope -> callableIn(scope).map { group -> group.filter { it.method.isNamed(name) }.toList() } }
            .firstOrNull { groups -> groups.any { it.isNotEmpty() } }
            .orEmpty()
    )

/**
 * Ranks one class's functions named [name], given as its static methods, its object's members and its
 * companion's. 4.1.0 used the first function of the first rank, but within a rank it took whichever
 * reflection listed first, and the JVM doesn't specify that order: it can change between runs.
 *
 * An `internal` function compiles to `name$module`, and only that compiled name used to find it, so
 * functions the old matching finds rank before those found by their Kotlin name alone. Then static
 * methods rank before an object's members, and those before a companion object's. A method with the
 * JVM name and parameters of one ranked before it is the same function, listed once: a `@JvmStatic`
 * function's companion member, or a bridge the compiler adds for an override's return type.
 */
internal fun rankOverloads(name: String, groups: List<List<StaticFunction>>): List<List<StaticFunction>> {
    val signatures = mutableSetOf<Pair<String, List<Class<*>>>>()
    return (groups.map { group -> group.filter { it.method.wasNamed(name) } } +
        groups.map { group -> group.filterNot { it.method.wasNamed(name) } })
        .map { rank ->
            rank.sortedBy { it.method.isBridge }
                .filter { signatures.add(it.method.name to it.method.parameterTypes.asList()) }
        }
}

/** Whether [name] exists only as an instance method, which nothing can call before the test instance exists. */
internal fun hasInstanceFunction(type: Class<*>, name: String): Boolean =
    hasInstanceFunction(enclosingClassesOf(type).toList(), name)

internal fun hasInstanceFunction(classes: List<Class<*>>, name: String): Boolean =
    scopesOf(classes).flatMap { it.declaredMethods.asSequence() }.any { it.isNamed(name) }

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

/**
 * Of the methods [isNamed] matches, the ones matched before `internal` names were understood: only a
 * value class's mangling, `name-<hash>`, was resolved then, so a `$` meant the compiled name.
 */
private fun Method.wasNamed(name: String): Boolean = this.name == name || '$' !in this.name || '-' in this.name

/** Static methods, then an object's members, then its companion object's: the order a name is looked for. */
private fun callableIn(scope: Class<*>): List<Sequence<StaticFunction>> = listOf(
    scope.declaredMethods.asSequence().filter { Modifier.isStatic(it.modifiers) }.map { StaticFunction(it, null) },
    membersOf(objectInstanceOf(scope)),
    membersOf(companionOf(scope))
)

private fun membersOf(instance: Any?): Sequence<StaticFunction> =
    if (instance == null) {
        emptySequence()
    } else {
        instance.javaClass.declaredMethods.asSequence().map { StaticFunction(it, instance) }
    }

/**
 * Each of [classes] with its superclasses, then its interfaces. Enclosing classes come after the
 * test class's own supertypes, so they never displace a function found there.
 */
private fun scopesOf(classes: List<Class<*>>): Sequence<Class<*>> =
    classes.asSequence().flatMap { supertypesOf(it) }.distinct()

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
