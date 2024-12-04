package com.wesleyhome.test.jupiter.annotations.validation.datetime

import java.time.temporal.TemporalAmount

object RandomDateTimeValidator {

    fun <T> validate(
        minString: String,
        maxString: String,
        size: Int,
        useOffset: Boolean,
        typeConvert: (String) -> T,
        offsetConvert: (String) -> TemporalAmount
    ): List<String> {
        val errors = mutableListOf<String>()
        if(minString.isBlank() ) {
            errors.add("Invalid [min] value: [$minString]")
        }
        if(maxString.isBlank()) {
            errors.add("Invalid [max] value: [$maxString]")
        }
        if(errors.isNotEmpty()) {
            return errors.toList()
        }
        if (useOffset) {
            try {
                offsetConvert(minString)
            } catch (_: Exception) {
                errors.add("Invalid [min] offset value: [$minString]")
            }
            try {
                offsetConvert(maxString)
            } catch (_: Exception) {
                errors.add("Invalid [max] offset value: [$maxString]")
            }
        } else {
            try {
                typeConvert(minString)
            }catch (_: Exception) {
                errors.add("Invalid [min] value: [$minString]")
            }
            try {
                typeConvert(maxString)
            } catch (_: Exception) {
                errors.add("Invalid [max] value: [$maxString]")
            }
        }
        if (size < 1) {
            errors.add("[size] must be greater than 0")
        }
        return errors.toList()
    }

}
