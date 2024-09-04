package com.wesleyhome.test.jupiter.annotations.validation.number

object NumberRangeValidator {

    fun validate(
        min: Number,
        max: Number,
        increment: Number?,
    ): List<String> {
        val errors = mutableListOf<String>()
        if(increment != null && increment <= 0) {
            errors.add("Increment must be greater than zero")
        }
        if(min >= max) {
            errors.add("Min value cannot be greater than max value")
        }
        return errors.toList()
    }
}

operator fun Number.compareTo(other: Number): Int {
    return this.toDouble().compareTo(other.toDouble())
}
