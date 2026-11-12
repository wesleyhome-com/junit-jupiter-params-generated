package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.InvalidValuesFromException
import com.wesleyhome.test.jupiter.MAX_PERMUTATIONS_PROPERTY
import com.wesleyhome.test.jupiter.annotations.ValuesFrom
import com.wesleyhome.test.jupiter.annotations.validation.ValuesFromReference
import com.wesleyhome.test.jupiter.generator.findStaticFunction
import com.wesleyhome.test.jupiter.generator.hasInstanceFunction
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.kotlinFunction

internal class ValuesFromDataProvider : ParameterDataProvider<Any> {

    override fun providesDataFor(testParameter: TestParameter): Boolean =
        testParameter.annotations.any { it is ValuesFrom }

    /** The extension calls [valuesFor] instead, because finding the function needs the test class. */
    override fun createParameterOptionsData(testParameter: TestParameter): List<Any?> =
        throw UnsupportedOperationException("@ValuesFrom needs the test class to find its function")

    /**
     * Reads no more than one value past [limit], so a function returning an endless [Sequence] fails
     * with a message instead of never returning.
     */
    fun valuesFor(testParameter: TestParameter, testClass: Class<*>, limit: Long): List<Any?> {
        val raw = testParameter.annotations.filterIsInstance<ValuesFrom>().first().value
        val reference = ValuesFromReference.parse(raw, testParameter.name)
            ?: throw InvalidValuesFromException(ValuesFromReference.malformed(raw, testParameter.name))
        val owner = reference.className?.let { load(it, testClass, reference) } ?: testClass
        val function = findStaticFunction(owner, reference.functionName) ?: throw InvalidValuesFromException(
            if (hasInstanceFunction(owner, reference.functionName)) {
                reference.instanceFunction()
            } else {
                reference.missingFunction(owner.simpleName)
            }
        )
        val method = function.method
        if (method.parameterCount != 0) {
            throw InvalidValuesFromException(reference.takesArguments(method.parameterCount))
        }
        checkDeclaredType(method, testParameter, reference)
        method.isAccessible = true
        val returned = try {
            method.invoke(function.receiver)
        } catch (e: InvocationTargetException) {
            throw InvalidValuesFromException(reference.threw(e.targetException), e.targetException)
        }
        val values = elementsOf(returned)
            ?.take(limit.coerceAtMost(Int.MAX_VALUE - 1L).toInt() + 1)
            ?.toList()
            ?: throw InvalidValuesFromException(reference.wrongReturnType("null"))
        if (values.size > limit) {
            throw InvalidValuesFromException(reference.tooMany(limit, MAX_PERMUTATIONS_PROPERTY))
        }
        values.forEachIndexed { position, value -> check(value, position, testParameter, reference) }
        return values
    }

    private fun load(className: String, testClass: Class<*>, reference: ValuesFromReference): Class<*> =
        try {
            Class.forName(className, true, testClass.classLoader)
        } catch (_: ClassNotFoundException) {
            throw InvalidValuesFromException(reference.missingClass())
        }

    private fun checkDeclaredType(method: Method, testParameter: TestParameter, reference: ValuesFromReference) {
        val returnType = method.returnType
        if (!Iterable::class.java.isAssignableFrom(returnType) &&
            !Sequence::class.java.isAssignableFrom(returnType) &&
            !returnType.isArray
        ) {
            throw InvalidValuesFromException(reference.wrongReturnType(returnType.kotlin.simpleName!!))
        }
        val element = method.kotlinFunction?.returnType?.let { elementTypeOf(it, returnType) } ?: return
        val elementClass = element.classifier as? KClass<*> ?: return
        if (!elementClass.isSubclassOf(testParameter.type)) {
            throw InvalidValuesFromException(
                reference.wrongElementType(elementClass.simpleName!!, testParameter.type.simpleName!!)
            )
        }
        if (element.isMarkedNullable && !testParameter.isNullable) {
            throw InvalidValuesFromException(reference.nullableElements())
        }
    }

    /** A `LongArray` has no type argument; its component type is the element. */
    private fun elementTypeOf(returnType: KType, jvmType: Class<*>): KType? =
        if (jvmType.isArray && jvmType.componentType.isPrimitive) {
            jvmType.componentType.kotlin.createType()
        } else {
            returnType.arguments.singleOrNull()?.type
        }

    /**
     * Catches what the declared type could not: a generic function or a Java raw type can return
     * anything, and a value that reached the test as the wrong type would fail far from its cause.
     */
    private fun check(value: Any?, position: Int, testParameter: TestParameter, reference: ValuesFromReference) {
        if (value == null) {
            if (!testParameter.isNullable) throw InvalidValuesFromException(reference.nullElement(position))
        } else if (!testParameter.type.javaObjectType.isInstance(value)) {
            throw InvalidValuesFromException(
                reference.wrongElement(value::class.simpleName!!, position, testParameter.type.simpleName!!)
            )
        }
    }

    private fun elementsOf(returned: Any?): Sequence<Any?>? = when (returned) {
        is Iterable<*> -> returned.asSequence()
        is Sequence<*> -> returned
        is Array<*> -> returned.asSequence()
        null -> null
        else -> (0 until java.lang.reflect.Array.getLength(returned)).asSequence()
            .map { java.lang.reflect.Array.get(returned, it) }
    }
}
