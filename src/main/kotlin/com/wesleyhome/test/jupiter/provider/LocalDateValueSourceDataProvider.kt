package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateSource
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeSource
import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalTimeSource
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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

object LocalTimeValueSourceDataProvider : AbstractParameterDataProvider<LocalTime>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalTime?> {
        val localTimeSource = findAnnotation(testParameter)!!
        return localTimeSource.values
            .map { it.toLocalTime(localTimeSource.timeFormat) }
            .toList()

    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalTimeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalTimeSource
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

object LocalTimeRangeDataProvider : AbstractParameterDataProvider<LocalTime>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<LocalTime?> {
        val s = findAnnotation(testParameter)!!
        val timeFormat = s.timeFormat
        val min = s.min.toLocalTime(timeFormat)
        val max = s.max.toLocalTime(timeFormat)
        val range = (min..max step s.increment).toList()
        return if(s.ascending) {
            range
        } else {
            range.reversed()
        }
    }

    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is LocalTimeRangeSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as LocalTimeRangeSource
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


class LocalTimeProgression(
    min: LocalTime,
    max: LocalTime,
    private val step: Duration
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


infix fun ClosedRange<LocalDateTime>.step(step: String): LocalDateTimeProgression {
    return LocalDateTimeProgression(this.start, this.endInclusive, step.duration())
}

infix fun ClosedRange<LocalDate>.step(step: String): LocalDateProgression {
    return LocalDateProgression(this.start, this.endInclusive, step.period())
}

infix fun ClosedRange<LocalTime>.step(step: String) : LocalTimeProgression {
    return LocalTimeProgression(this.start, this.endInclusive, step.duration())
}

fun String.period(): Period = Period.parse(this)
fun String.duration(): Duration = Duration.parse(this)
fun String.toLocalDate(dateFormat: String = "yyyy-MM-dd"): LocalDate =
    LocalDate.parse(this, DateTimeFormatter.ofPattern(dateFormat))

fun String.toLocalDateTime(dateTimeFormat: String = "yyyy-MM-dd HH:mm"): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(dateTimeFormat))

fun String.toLocalTime(timeFormat: String = "HH:mm") : LocalTime =
    LocalTime.parse(this, DateTimeFormatter.ofPattern(timeFormat))
