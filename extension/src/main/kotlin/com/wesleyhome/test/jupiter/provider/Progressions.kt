package com.wesleyhome.test.jupiter.provider

import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAmount
import java.time.temporal.UnsupportedTemporalTypeException
import java.util.concurrent.atomic.AtomicReference


internal class LocalDateTimeProgression(
    min: LocalDateTime, max: LocalDateTime, private val step: TemporalAmount
) : Iterable<LocalDateTime> {

    private val range = min..max
    private val current = AtomicReference<LocalDateTime?>(min)

    override fun iterator(): Iterator<LocalDateTime> {
        return object : Iterator<LocalDateTime> {
            override fun hasNext(): Boolean = current.get()?.let(range::contains) == true

            override fun next(): LocalDateTime {
                return current.getAndUpdate { it?.stepOrNull { value -> value + step } } ?: throw NoSuchElementException()
            }
        }
    }
}

internal class LocalDateProgression(
    min: LocalDate, max: LocalDate, private val step: TemporalAmount
) : Iterable<LocalDate> {

    private val range = min..max
    private val current = AtomicReference<LocalDate?>(min)

    override fun iterator(): Iterator<LocalDate> {
        return object : Iterator<LocalDate> {
            override fun hasNext(): Boolean = current.get()?.let(range::contains) == true

            override fun next(): LocalDate {
                return current.getAndUpdate { it?.stepOrNull { value -> value + step } } ?: throw NoSuchElementException()
            }
        }
    }
}


internal class LocalTimeProgression(
    min: LocalTime, max: LocalTime, private val step: TemporalAmount
) : Iterable<LocalTime> {

    private val range = min..max
    private val current = AtomicReference(min)

    override fun iterator(): Iterator<LocalTime> {
        return object : Iterator<LocalTime> {
            override fun hasNext(): Boolean = range.contains(current.get())

            override fun next(): LocalTime {
                return current.getAndUpdate { it + step }
            }
        }
    }
}

internal class InstantProgression(
    min: Instant, max: Instant, private val step: TemporalAmount
) : Iterable<Instant> {

    private val range = min..max
    private val current = AtomicReference<Instant?>(min)

    override fun iterator(): Iterator<Instant> {
        return object : Iterator<Instant> {
            override fun hasNext(): Boolean = current.get()?.let(range::contains) == true

            override fun next(): Instant {
                return current.getAndUpdate { it?.stepOrNull { value -> value + step } } ?: throw NoSuchElementException()
            }
        }
    }
}

/**
 * Null once a step leaves what the type can hold, as one past `LocalDate.MAX` does, which ends the
 * progression. A step the type cannot take at all, such as a month on an `Instant`, still throws.
 */
private fun <T> T.stepOrNull(step: (T) -> T): T? =
    try {
        step(this)
    } catch (e: UnsupportedTemporalTypeException) {
        throw e
    } catch (_: DateTimeException) {
        null
    } catch (_: ArithmeticException) {
        null
    }
