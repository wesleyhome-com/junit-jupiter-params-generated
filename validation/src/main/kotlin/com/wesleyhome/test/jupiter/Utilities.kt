@file:Suppress("UNCHECKED_CAST")

package com.wesleyhome.test.jupiter

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAmount
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties


fun String.period(): Period = Period.parse(this)
fun String.duration(): Duration = Duration.parse(this)
fun String.temporalAmount(): TemporalAmount =
    try {
        this.period()
    } catch (_: DateTimeParseException) {
        this.duration()
    }

fun String.formatter(): DateTimeFormatter = DateTimeFormatter.ofPattern(this)

fun String.toLocalDate(dateFormat: String = "yyyy-MM-dd"): LocalDate =
    LocalDate.parse(this, dateFormat.formatter())

fun String.toLocalDateTime(dateTimeFormat: String = "yyyy-MM-dd HH:mm"): LocalDateTime =
    LocalDateTime.parse(this, dateTimeFormat.formatter())

fun String.toLocalTime(timeFormat: String = "HH:mm"): LocalTime =
    LocalTime.parse(this, timeFormat.formatter())

fun <T> Annotation.propertyValue(propertyName: String): T =
    this::class.memberProperties.first { it.name == propertyName }.getter.call(this) as T

val Any.typeArguments: Array<Type>
    get() = (this.javaClass.genericSuperclass as ParameterizedType)
        .actualTypeArguments

fun <T : Any> Type.kotlinType(): KClass<T> {
    return (this as Class<T>).kotlin
}

/**
 * The concrete class this object binds to the type parameter at [index] of [declaringClass].
 *
 * Unlike [typeArguments], which reads `javaClass.genericSuperclass` and so only works when the
 * receiver extends [declaringClass] directly, this walks the whole superclass chain and carries
 * type variable bindings along the way. A class hierarchy of any depth between the receiver and
 * [declaringClass] resolves, including links that pass the variable through or erase it.
 *
 * @throws IllegalStateException if the receiver does not extend [declaringClass], or the binding
 * cannot be resolved to a class because the hierarchy discards it.
 */
fun <T : Any> Any.resolveTypeArgument(declaringClass: Class<*>, index: Int): KClass<T> {
    var current: Class<*> = javaClass
    var bindings: Map<Type, Type> = emptyMap()
    while (current != Any::class.java) {
        val genericSuperclass = current.genericSuperclass ?: break
        val superclass = genericSuperclass.rawClass()
        bindings = if (genericSuperclass is ParameterizedType) {
            val actualArguments = genericSuperclass.actualTypeArguments.map { bindings[it] ?: it }
            superclass.typeParameters.withIndex()
                .associateTo(mutableMapOf<Type, Type>()) { (i, typeParameter) ->
                    typeParameter to actualArguments[i]
                }
        } else {
            emptyMap()
        }
        if (superclass == declaringClass) {
            val typeParameter = declaringClass.typeParameters.getOrNull(index)
                ?: error("${declaringClass.name} does not declare a type parameter at index $index")
            val resolved = bindings[typeParameter]
                ?: error(
                    "Unable to resolve type parameter [${typeParameter.name}] of ${declaringClass.name} " +
                        "from ${javaClass.name}"
                )
            return resolved.rawClass().kotlin as KClass<T>
        }
        current = superclass
    }
    error("${javaClass.name} does not extend ${declaringClass.name}")
}

private fun Type.rawClass(): Class<*> = when (this) {
    is Class<*> -> this
    is ParameterizedType -> rawType as Class<*>
    else -> error("Unable to resolve [$this] to a class")
}
