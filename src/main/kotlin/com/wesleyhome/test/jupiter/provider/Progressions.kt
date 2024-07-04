package com.wesleyhome.test.jupiter.provider

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.util.concurrent.atomic.AtomicReference


class LocalDateTimeProgression(
    min: LocalDateTime, max: LocalDateTime, private val step: Duration
) : Iterable<LocalDateTime> {

    private val range = min..max
    private val current = AtomicReference(min)

    override fun iterator(): Iterator<LocalDateTime> {
        return object : Iterator<LocalDateTime> {
            override fun hasNext(): Boolean = range.contains(current.get())

            override fun next(): LocalDateTime {
                return current.getAndUpdate { it + step }
            }
        }
    }
}

class LocalDateProgression(
    min: LocalDate, max: LocalDate, private val step: Period
) : Iterable<LocalDate> {

    private val range = min..max
    private val current = AtomicReference(min)

    override fun iterator(): Iterator<LocalDate> {
        return object : Iterator<LocalDate> {
            override fun hasNext(): Boolean = range.contains(current.get())

            override fun next(): LocalDate {
                return current.getAndUpdate { it + step }
            }
        }
    }
}


class LocalTimeProgression(
    min: LocalTime, max: LocalTime, private val step: Duration
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
