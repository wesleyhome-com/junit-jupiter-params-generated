package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.provider.number.DoubleProgression
import com.wesleyhome.test.jupiter.provider.number.FloatProgression
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAmount


infix fun ClosedRange<LocalDateTime>.step(step: String): LocalDateTimeProgression {
    return LocalDateTimeProgression(this.start, this.endInclusive, step.temporalAmount())
}

infix fun ClosedRange<LocalDate>.step(step: String): LocalDateProgression {
    return LocalDateProgression(this.start, this.endInclusive, step.temporalAmount())
}

infix fun ClosedRange<LocalTime>.step(step: String) : LocalTimeProgression {
    return LocalTimeProgression(this.start, this.endInclusive, step.temporalAmount())
}

infix fun ClosedRange<Instant>.step(step: String) : InstantProgression {
    return InstantProgression(this.start, this.endInclusive, step.temporalAmount())
}
infix fun ClosedFloatingPointRange<Double>.step(step: Double): DoubleProgression {
    return DoubleProgression(this.start, this.endInclusive, step)
}

infix fun ClosedFloatingPointRange<Float>.step(step: Float): FloatProgression {
    return FloatProgression(this.start, this.endInclusive, step)
}

fun String.period(): Period = Period.parse(this)
fun String.duration(): Duration = Duration.parse(this)
fun String.temporalAmount(): TemporalAmount =
    try {
        this.period()
    } catch (e: DateTimeParseException) {
        this.duration()
    }

fun String.toLocalDate(dateFormat: String = "yyyy-MM-dd"): LocalDate =
    LocalDate.parse(this, DateTimeFormatter.ofPattern(dateFormat))

fun String.toLocalDateTime(dateTimeFormat: String = "yyyy-MM-dd HH:mm"): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(dateTimeFormat))

fun String.toLocalTime(timeFormat: String = "HH:mm") : LocalTime =
    LocalTime.parse(this, DateTimeFormatter.ofPattern(timeFormat))
