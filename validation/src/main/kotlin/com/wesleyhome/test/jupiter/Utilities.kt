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
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
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

const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"
const val DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm"

private val EXTENDED_DATE: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.SMART)

private val EXTENDED_DATE_TIME: DateTimeFormatter = DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .appendPattern("HH:mm")
    .toFormatter()
    .withResolverStyle(ResolverStyle.SMART)

/**
 * A formatter for this pattern. The two default patterns parse everything they always have, down to
 * resolving `2024-02-30` to `2024-02-29`, and also the year 0 and negative years, so `LocalDate.MIN` can
 * be written as `-999999999-01-01`.
 */
fun String.formatter(): DateTimeFormatter = when (this) {
    DEFAULT_DATE_FORMAT -> EXTENDED_DATE
    DEFAULT_DATE_TIME_FORMAT -> EXTENDED_DATE_TIME
    else -> DateTimeFormatter.ofPattern(this)
}

fun String.toLocalDate(dateFormat: String = DEFAULT_DATE_FORMAT): LocalDate =
    LocalDate.parse(this, dateFormat.formatter())

fun String.toLocalDateTime(dateTimeFormat: String = DEFAULT_DATE_TIME_FORMAT): LocalDateTime =
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
