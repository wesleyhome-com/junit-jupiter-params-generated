package com.wesleyhome.test.jupiter

import com.wesleyhome.test.jupiter.provider.InstantProgression
import com.wesleyhome.test.jupiter.provider.LocalDateProgression
import com.wesleyhome.test.jupiter.provider.LocalDateTimeProgression
import com.wesleyhome.test.jupiter.provider.LocalTimeProgression
import com.wesleyhome.test.jupiter.provider.number.DoubleProgression
import com.wesleyhome.test.jupiter.provider.number.FloatProgression
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

internal infix fun ClosedFloatingPointRange<Float>.step(step: Float): FloatProgression {
    return FloatProgression(this.start, this.endInclusive, step)
}

internal infix fun ClosedRange<LocalDateTime>.step(step: String): LocalDateTimeProgression {
    return LocalDateTimeProgression(this.start, this.endInclusive, step.temporalAmount())
}

internal infix fun ClosedRange<LocalDate>.step(step: String): LocalDateProgression {
    return LocalDateProgression(this.start, this.endInclusive, step.temporalAmount())
}

internal infix fun ClosedRange<LocalTime>.step(step: String): LocalTimeProgression {
    return LocalTimeProgression(this.start, this.endInclusive, step.temporalAmount())
}

internal infix fun ClosedRange<Instant>.step(step: String): InstantProgression {
    return InstantProgression(this.start, this.endInclusive, step.temporalAmount())
}

internal infix fun ClosedFloatingPointRange<Double>.step(step: Double): DoubleProgression {
    return DoubleProgression(this.start, this.endInclusive, step)
}
