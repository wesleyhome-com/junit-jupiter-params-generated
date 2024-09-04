package com.wesleyhome.test.jupiter.annotations.validation.datetime

import com.wesleyhome.test.jupiter.formatter
import com.wesleyhome.test.jupiter.temporalAmount
import java.time.format.DateTimeParseException

object DateTimeRangeValidator {

    fun <T : Comparable<T>> validate(
        minString: String,
        maxString: String,
        increment: String?,
        dateFormat: String?,
        transformer: (String, String) -> T
    ): List<String> {
        val errors = mutableListOf<String>()
        val format = if (dateFormat != null) {
            try {
                dateFormat.formatter()
                dateFormat
            } catch (e: Exception) {
                errors.add("Invalid date format: [$dateFormat]")
                return errors.toList()
            }
        } else {
            "yyyy-MM-dd"
        }
        val min = try {
            transformer(minString, format)
        } catch (e: DateTimeParseException) {
            errors.add("Unable to parse min string [$minString] using format [$format]")
            null
        }
        val max = try {
            transformer(maxString, format)
        } catch (e: DateTimeParseException) {
            errors.add("Unable to parse max string [$maxString] using format [$format]")
            null
        }
        if (min != null && max != null && min >= max) {
            errors.add("Min value [$minString] must be less than max value [$maxString]")
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
