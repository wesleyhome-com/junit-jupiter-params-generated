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
