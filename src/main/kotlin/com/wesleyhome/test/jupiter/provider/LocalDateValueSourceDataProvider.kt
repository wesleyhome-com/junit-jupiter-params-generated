package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateSource
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeSource
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicReference

object LocalDateValueSourceDataProvider : AbstractParameterDataProvider<LocalDate>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDate?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDate(localDateSource.dateFormat) }
            .toList()
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalDateSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalDateSource
            }
        }
}

object LocalDateTimeValueSourceDataProvider : AbstractParameterDataProvider<LocalDateTime>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDateTime?> {
        val localDateSource = findAnnotation(testParameter)!!
        return localDateSource.values
            .map { it.toLocalDateTime(localDateSource.dateTimeFormat) }
            .toList()
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalDateTimeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalDateTimeSource
            }
        }
}

object LocalDateRangeDataProvider : AbstractParameterDataProvider<LocalDate>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDate?> {
        val s = findAnnotation(testParameter)!!
        val dateFormat = s.dateFormat
        val range = (s.min.toLocalDate(dateFormat)..s.max.toLocalDate(dateFormat) step s.increment).toList()
        return if (s.ascending) {
            range
        } else {
            range.reversed()
        }
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalDateRangeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalDateRangeSource
            }
        }
}

object LocalDateTimeRangeDataProvider : AbstractParameterDataProvider<LocalDateTime>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalDateTime?> {
        val s = findAnnotation(testParameter)!!
        val dateFormat = s.dateTimeFormat
        val range = (s.min.toLocalDateTime(dateFormat)..s.max.toLocalDateTime(dateFormat) step s.increment).toList()
        return if (s.ascending) {
            range
        } else {
            range.reversed()
        }
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalDateTimeRangeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalDateTimeRangeSource
            }
        }
}

class LocalDateProgression(
    min: LocalDate,
    max: LocalDate,
    private val step: Period
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

class LocalDateTimeProgression(
    min: LocalDateTime,
    max: LocalDateTime,
    private val step: Duration
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

infix fun ClosedRange<LocalDateTime>.step(step: String): LocalDateTimeProgression {
    return LocalDateTimeProgression(this.start, this.endInclusive, step.duration())
}

infix fun ClosedRange<LocalDate>.step(step: String): LocalDateProgression {
    return LocalDateProgression(this.start, this.endInclusive, step.period())
}

fun String.period(): Period = Period.parse(this)
fun String.duration(): Duration = Duration.parse(this)
fun String.toLocalDate(dateFormat: String = "yyyy-MM-dd"): LocalDate =
    LocalDate.parse(this, DateTimeFormatter.ofPattern(dateFormat))

fun String.toLocalDateTime(dateTimeFormat: String = "yyyy-MM-dd HH:mm"): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(dateTimeFormat))
