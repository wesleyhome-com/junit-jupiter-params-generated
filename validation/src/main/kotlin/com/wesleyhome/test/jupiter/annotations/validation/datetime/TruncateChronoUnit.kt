package com.wesleyhome.test.jupiter.annotations.validation.datetime

import java.time.temporal.ChronoUnit

/**
 * Valid [java.time.temporal.ChronoUnit] values to use with [truncateTo] properties
 */
enum class TruncateChronoUnit(val chronoUnit: ChronoUnit) {
    NANOS(ChronoUnit.NANOS),
    MICROS(ChronoUnit.MICROS),
    MILLIS(ChronoUnit.MILLIS),
    SECONDS(ChronoUnit.SECONDS),
    MINUTES(ChronoUnit.MINUTES),
    HOURS(ChronoUnit.HOURS),
    HALF_DAYS(ChronoUnit.HALF_DAYS),
    DAYS(ChronoUnit.DAYS),
}
