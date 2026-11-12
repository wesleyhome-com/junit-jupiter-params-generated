package com.wesleyhome.test.jupiter.provider

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import com.wesleyhome.test.jupiter.step
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.UnsupportedTemporalTypeException

/** Stepping past a type's last value overflows it, so a range ending there has to stop first. */
class ProgressionsTest {

    @Test
    fun testLocalDateProgressionEndsAtMax() {
        assertThat((LocalDate.MAX.minusDays(1)..LocalDate.MAX step "P1d").toList())
            .containsExactly(LocalDate.MAX.minusDays(1), LocalDate.MAX)
    }

    @Test
    fun testLocalDateTimeProgressionEndsAtMax() {
        assertThat((LocalDateTime.MAX.minusHours(1)..LocalDateTime.MAX step "PT1h").toList())
            .containsExactly(LocalDateTime.MAX.minusHours(1), LocalDateTime.MAX)
    }

    @Test
    fun testInstantProgressionEndsAtMax() {
        assertThat((Instant.MAX.minusSeconds(3600)..Instant.MAX step "PT1h").toList())
            .containsExactly(Instant.MAX.minusSeconds(3600), Instant.MAX)
    }

    @Test
    fun testInstantProgressionEndsAtItsMaxAsBefore() {
        val start = Instant.parse("2024-01-01T00:00:00Z")
        assertThat((start..start.plusSeconds(9_000) step "PT1h").toList())
            .containsExactly(start, start.plusSeconds(3_600), start.plusSeconds(7_200))
    }

    /** Only overflow ends a progression quietly; a step the type cannot take is still an error. */
    @Test
    fun testUnsupportedStepStillThrows() {
        val start = Instant.parse("2024-01-01T00:00:00Z")
        assertFailure { (start..start.plusSeconds(86_400) step "P1m").toList() }
            .isInstanceOf(UnsupportedTemporalTypeException::class)
    }

    /** A step this large overflows the seconds count itself rather than the instant's range. */
    @Test
    fun testInstantProgressionEndsWhenTheStepOverflowsArithmetic() {
        assertThat((Instant.MAX..Instant.MAX step "PT${Long.MAX_VALUE}S").toList()).containsExactly(Instant.MAX)
    }

    @Test
    fun testExhaustedProgressionThrowsNoSuchElement() {
        listOf(
            (LocalDate.MAX..LocalDate.MAX step "P1d").iterator(),
            (LocalDateTime.MAX..LocalDateTime.MAX step "PT1h").iterator(),
            (Instant.MAX..Instant.MAX step "PT1h").iterator()
        ).forEach { iterator ->
            iterator.next()
            assertThat(iterator.hasNext()).isFalse()
            assertFailure { iterator.next() }.isInstanceOf(NoSuchElementException::class)
        }
    }
}
