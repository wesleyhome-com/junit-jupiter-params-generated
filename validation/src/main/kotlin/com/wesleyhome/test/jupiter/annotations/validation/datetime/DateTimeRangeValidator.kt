package com.wesleyhome.test.jupiter.annotations.validation.datetime

import com.wesleyhome.test.jupiter.formatter
import com.wesleyhome.test.jupiter.temporalAmount
import java.time.format.DateTimeParseException

object DateTimeRangeValidator {

    /**
     * @param dateFormat the format [parse] reads values with, or null for a type that has no format, such as
     * an ISO-8601 instant.
     */
    fun <T : Comparable<T>> validate(
        minString: String,
        maxString: String,
        increment: String?,
        dateFormat: String?,
        parse: (String) -> T
    ): List<String> {
        val errors = mutableListOf<String>()
        if (dateFormat != null) {
            try {
                dateFormat.formatter()
            } catch (e: Exception) {
                errors.add("Invalid date format: [$dateFormat]")
                return errors.toList()
            }
        }
        val usingFormat = dateFormat?.let { " using format [$it]" }.orEmpty()
        val min = try {
            parse(minString)
        } catch (e: DateTimeParseException) {
            errors.add("Unable to parse min string [$minString]$usingFormat")
            null
        }
        val max = try {
            parse(maxString)
        } catch (e: DateTimeParseException) {
            errors.add("Unable to parse max string [$maxString]$usingFormat")
            null
        }
        if (min != null && max != null && min > max) {
            errors.add("Min value [$minString] cannot be greater than max value [$maxString]")
        }
        if (increment != null) {
            try {
                increment.temporalAmount()
            } catch (e: DateTimeParseException) {
                errors.add("Unable to parse increment [$increment] into a duration or period")
            }
        }
        return errors.toList()
    }
}
