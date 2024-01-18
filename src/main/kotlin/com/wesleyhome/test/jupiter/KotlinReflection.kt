package com.wesleyhome.test.jupiter

import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.javaMethod


fun KClass<*>.allFunctions(): Collection<KFunction<*>> = this.declaredFunctions.toSet() + this.declaredMemberFunctions

fun KFunction<*>.isStatic(): Boolean = this.javaMethod?.let { Modifier.isStatic(it.modifiers) } ?: false

fun KFunction<*>.isNotStatic(): Boolean = !this.isStatic()

fun KFunction<*>.isStaticNonTestInstance(providerClass: KClass<*>): Boolean =
    this.isStatic() && this.declaredKClass() == providerClass

fun KFunction<*>.declaredKClass() = this.javaMethod?.declaringClass?.kotlin

fun KFunction<*>.isInstanceMethodOfTestClass(testClass: KClass<*>): Boolean {
    val notStatic = this.isNotStatic()
    val assignableFrom = this.declaredKClass()?.java?.isAssignableFrom(testClass.java) ?: false
    return notStatic && assignableFrom
}

fun KFunction<*>.isNoArg(): Boolean = this.parameters.drop(1).isEmpty()

fun KFunction<*>.returnsIterable(parameter: KParameter): Boolean {
    val returnType = this.returnType
    val isIterable = returnType.isSubtypeOf(Iterable::class.starProjectedType)
    return if (isIterable) {
        val arguments = returnType.arguments
        if (arguments.isNotEmpty()) {
            val type = arguments[0].type
            if (type != null) {
                val parameterType = parameter.type
                val sameType = parameterType == type
                val subtype = parameterType.isSubtypeOf(type)
                val otherSubtype = type.isSubtypeOf(parameterType)
                sameType || (subtype && otherSubtype)
            } else {
                false
            }
        } else {
            false
        }
    } else {
        false
    }
}
