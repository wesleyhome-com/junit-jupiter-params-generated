package com.wesleyhome.test.jupiter.annotations.processor

import com.wesleyhome.test.jupiter.annotations.validation.datetime.DateTimeRangeValidator
import com.wesleyhome.test.jupiter.annotations.validation.number.NumberRangeValidator
import com.wesleyhome.test.jupiter.toLocalDate
import com.wesleyhome.test.jupiter.toLocalDateTime
import com.wesleyhome.test.jupiter.toLocalTime
import java.time.Instant

/**
 * The compile-time half of range validation, expressed over plain argument values so it can be
 * tested without a KSP compilation and so the processor itself stays glue. The rules come from the
 * same validators the runtime uses, which is what keeps the two from drifting apart.
 */
internal object RangeAnnotationValidator {

    fun validate(annotationName: String, arguments: Map<String, Any?>): List<String> = when {
        annotationName in NUMBER_RANGES -> validateNumberRange(arguments)
        else -> DATE_TIME_RANGES[annotationName]?.validate(arguments) ?: emptyList()
    }

    private val NUMBER_RANGES = setOf(
        "IntRangeSource",
        "LongRangeSource",
        "FloatRangeSource",
        "DoubleRangeSource"
    )

    private val DATE_TIME_RANGES = mapOf(
        "LocalDateRangeSource" to DateTimeRange("dateFormat", "yyyy-MM-dd", "P1d", String::toLocalDate),
        "LocalDateTimeRangeSource" to
            DateTimeRange("dateTimeFormat", "yyyy-MM-dd HH:mm", "PT1h", String::toLocalDateTime),
        "LocalTimeRangeSource" to DateTimeRange("timeFormat", "HH:mm", "PT1h", String::toLocalTime),
        "InstantRangeSource" to DateTimeRange(null, null, "PT1h") { value, _ -> Instant.parse(value) }
    )

    private fun validateNumberRange(arguments: Map<String, Any?>): List<String> {
        val min = arguments["min"] as? Number ?: return missing("min")
        val max = arguments["max"] as? Number ?: return missing("max")
        return NumberRangeValidator.validate(min, max, arguments["increment"] as? Number)
    }

    private fun missing(argument: String) = listOf("Unable to read [$argument]")

    private class DateTimeRange<T : Comparable<T>>(
        private val formatArgument: String?,
        private val defaultFormat: String?,
        private val defaultIncrement: String,
        private val parse: (String, String) -> T
    ) {
        fun validate(arguments: Map<String, Any?>): List<String> {
            val min = arguments["min"] as? String ?: return missing("min")
            val max = arguments["max"] as? String ?: return missing("max")
            val increment = arguments["increment"] as? String ?: defaultIncrement
            val format = formatArgument?.let { arguments[it] as? String ?: defaultFormat }
            return DateTimeRangeValidator.validate(min, max, increment, format, parse)
        }
    }
}
