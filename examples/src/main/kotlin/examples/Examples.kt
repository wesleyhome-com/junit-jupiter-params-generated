package examples

import assertk.Assert
import assertk.all
import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isGreaterThan
import assertk.assertions.isIn
import assertk.assertions.isLessThan
import assertk.assertions.support.expected
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.datetime.InstantRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.InstantSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateTimeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.datetime.LocalTimeSource
import com.wesleyhome.test.jupiter.annotations.datetime.RandomInstantSource
import com.wesleyhome.test.jupiter.annotations.datetime.TruncateChronoUnit
import com.wesleyhome.test.jupiter.annotations.number.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.number.DoubleSource
import com.wesleyhome.test.jupiter.annotations.number.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.number.FloatSource
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource
import com.wesleyhome.test.jupiter.annotations.number.LongSource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun Assert<Number>.isOdd() = given { actual ->
    if (actual.toInt() % 2 == 0) {
        expected("Expected $actual to be odd")
    }

}

class IntRangeSource {

    fun example() {

        /**
         * This will generate 300 tests with the values 1 to 300
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testIntRangeSource(@IntRangeSource(min = 1, max = 300) value: Int) {
            assertThat(value).isBetween(1, 300)
        }

        /**
         * This will generate 149 tests with the values 1 to 300 with an increment of 2
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testIntRangeSourceWithIncrement(@IntRangeSource(min = 1, max = 300, increment = 2) value: Int) {
            assertThat(value).all {
                isBetween(1, 300)
                isOdd()
            }
        }

        /**
         * This will generate 149 tests with the values 1 to 300 with an increment of 2
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testIntRangeSourceWithIncrementDescending(
            @IntRangeSource(
                min = 1,
                max = 300,
                increment = 2,
                ascending = false
            ) value: Int
        ) {
            assertThat(value).all {
                isBetween(1, 300)
                isOdd()
            }
        }
    }
}

class LongRangeSource {

    fun example() {

        /**
         * This will generate 300 tests with the values 1 to 300
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testLongRangeSource(@LongRangeSource(min = 1, max = 300) value: Int) {
            assertThat(value).isBetween(1, 300)
        }

        /**
         * This will generate 149 tests with the values 1 to 300 with an increment of 2
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testLongRangeSourceWithIncrement(@LongRangeSource(min = 1, max = 300, increment = 2) value: Int) {
            assertThat(value).all {
                isBetween(1, 300)
                isOdd()
            }
        }

        /**
         * This will generate 149 tests with the values 1 to 300 with an increment of 2
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testLongRangeSourceWithIncrementDescending(
            @LongRangeSource(
                min = 1,
                max = 300,
                increment = 2,
                ascending = false
            ) value: Int
        ) {
            assertThat(value).all {
                isBetween(1, 300)
                isOdd()
            }
        }
    }
}

class DoubleRangeSource {

    fun example() {

        /**
         * This will generate 600 tests with the values 1 to 300 incrementing by 0.5.
         * The values will be in ascending order.
         */
        @GeneratedParametersTest
        fun testDoubleRangeSource(@DoubleRangeSource(min = 1.0, max = 300.0) value: Int) {
            assertThat(value).isBetween(1, 300)
        }

        /**
         * This will generate 149 tests with the values 1 to 300 with an increment of 2
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testDoubleRangeSourceWithIncrement(@DoubleRangeSource(min = 1.0, max = 300.0, increment = 2.0) value: Int) {
            assertThat(value).all {
                isBetween(1, 300)
                isOdd()
            }
        }

        /**
         * This will generate 149 tests with the values 1 to 300 with an increment of 2
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testDoubleRangeSourceWithIncrementDescending(
            @DoubleRangeSource(
                min = 1.0,
                max = 300.0,
                increment = 2.0,
                ascending = false
            ) value: Int
        ) {
            assertThat(value).all {
                isBetween(1, 300)
                isOdd()
            }
        }
    }
}

class FloatRangeSource {

    fun example() {

        /**
         * This will generate 600 tests with the values 1.0 to 300.0
         * incrementing by 0.5. The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testFloatRangeSource(@FloatRangeSource(min = 1.0F, max = 300.0F) value: Int) {
            assertThat(value).isBetween(1, 300)
        }

        /**
         * This will generate 300 tests with the values 1 to 300 with an increment of 1
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testFloatRangeSourceWithIncrement(
            @FloatRangeSource(
                min = 1.0F,
                max = 300.0F,
                increment = 1.0F
            ) value: Int
        ) {
            assertThat(value).all {
                isBetween(1, 300)
            }
        }

        /**
         * This will generate 300 tests with the values 1 to 300 with an increment of 1
         * The values will be in ascending order
         */
        @GeneratedParametersTest
        fun testFloatRangeSourceWithIncrementDescending(
            @FloatRangeSource(
                min = 1.0F,
                max = 300.0F,
                increment = 1.0F,
                ascending = false
            ) value: Int
        ) {
            assertThat(value).all {
                isBetween(1, 300)
            }
        }
    }
}

class IntSource {

    fun example() {

        /**
         * This will generate 4 tests with the values 1 - 3, 5
         */
        @GeneratedParametersTest
        fun testIntSource(@IntSource(values = [1, 2, 3, 5]) value: Int) {
            assertThat(value).isIn(1, 2, 3, 5)
        }
    }
}

class LongSource {

    fun example() {

        /**
         * This will generate 4 tests with the values 1 - 3, 5
         */
        @GeneratedParametersTest
        fun testLongSource(@LongSource(values = [1L, 2L, 3L, 5L]) value: Int) {
            assertThat(value).isIn(1L, 2L, 3L, 5L)
        }
    }
}

class DoubleSource {

    fun example() {

        /**
         * This will generate 4 tests with the values 1 - 3, 5
         */
        @GeneratedParametersTest
        fun testDoubleSource(@DoubleSource(values = [1.0, 2.0, 3.0, 5.0]) value: Int) {
            assertThat(value).isIn(1.0, 2.0, 3.0, 5.0)
        }
    }
}

class FloatSource {

    fun example() {

        /**
         * This will generate 4 tests with the values 1 - 3, 5
         */
        @GeneratedParametersTest
        fun testFloatSource(@FloatSource(values = [1.0f, 2.0f, 3.0f, 5.0f]) value: Int) {
            assertThat(value).isIn(1.0f, 2.0f, 3.0f, 5.0f)
        }
    }
}

class StringSource {

    fun example() {

        /**
         * This will generate 4 tests with the values "one", "two", "three", "four"
         */
        @GeneratedParametersTest
        fun testStringSource(@StringSource(values = ["one", "two", "three", "four"]) value: Int) {
            assertThat(value).isIn("one", "two", "three", "four")
        }
    }
}

class LocalDateSource {

    fun example() {

        /**
         * This will generate 5 tests with the values "2020-01-01", "2021-01-01", "2022-01-01", "2023-01-01", "2024-01-01"
         */
        @GeneratedParametersTest
        fun testLocalDateSource(@LocalDateSource(values = ["2020-01-01", "2021-01-01", "2022-01-01", "2023-01-01", "2024-01-01"]) value: LocalDate) {
            assertThat(value).isLessThan(LocalDate.now())
        }

        /**
         * This will generate 5 tests with the values "01/01/2020", "01/01/2021", "01/01/2022", "01/01/2023", "01/01/2024"
         * The values will be parsed using the format "MM/dd/yyyy"
         */
        @GeneratedParametersTest
        fun testLocalDateSourceWithFormat(
            @LocalDateSource(
                values = ["01/01/2020", "01/01/2021", "01/01/2022", "01/01/2023", "01/01/2024"],
                dateFormat = "MM/dd/yyyy"
            )
            value: LocalDate
        ) {
            assertThat(value).isLessThan(LocalDate.now())
        }
    }
}

class LocalDateTimeSource {

    fun example() {

        /**
         * This will generate 5 tests with the values "2020-01-01", "2021-01-01", "2022-01-01", "2023-01-01", "2024-01-01"
         */
        @GeneratedParametersTest
        fun testLocalDateTimeSource(
            @LocalDateTimeSource(
                values = [
                    "2020-01-01 00:00",
                    "2021-01-01 02:00",
                    "2022-01-01 04:00",
                    "2023-01-01 06:00",
                    "2024-01-01 08:00"
                ]
            )
            value: LocalDateTime
        ) {
            assertThat(value).isLessThan(LocalDateTime.now())
        }

        /**
         * This will generate 5 tests with the values "01/01/2020", "01/01/2021", "01/01/2022", "01/01/2023", "01/01/2024"
         * The values will be parsed using the format "MM/dd/yyyy"
         */
        @GeneratedParametersTest
        fun testLocalDateTimeSourceWithFormat(
            @LocalDateTimeSource(
                values = [
                    "01/01/2020 00:00:01",
                    "01/01/2021 02:00:02",
                    "01/01/2022 03:00:03",
                    "01/01/2023 04:00:04",
                    "01/01/2024 05:00:05"
                ],
                dateTimeFormat = "MM/dd/yyyy hh:mm:ss"
            )
            value: LocalDateTime
        ) {
            assertThat(value).isLessThan(LocalDateTime.now())
        }
    }
}

class LocalTimeSource {

    fun example() {

        /**
         * This will generate 5 tests with the values "2020-01-01", "2021-01-01", "2022-01-01", "2023-01-01", "2024-01-01"
         */
        @GeneratedParametersTest
        fun testLocalTimeSource(
            @LocalTimeSource(
                values = [
                    "00:00",
                    "02:00",
                    "04:00",
                    "06:00",
                    "08:00"
                ]
            )
            value: LocalTime
        ) {
            assertThat(value).isLessThan(LocalTime.parse("09:00"))
        }

        /**
         * This will generate 5 tests with the values "01/01/2020", "01/01/2021", "01/01/2022", "01/01/2023", "01/01/2024"
         * The values will be parsed using the format "MM/dd/yyyy"
         */
        @GeneratedParametersTest
        fun testLocalTimeSourceWithFormat(
            @LocalTimeSource(
                values = [
                    "00:00:01",
                    "02:00:02",
                    "03:00:03",
                    "04:00:04",
                    "05:00:05"
                ],
                timeFormat = "hh:mm:ss"
            )
            value: LocalTime
        ) {
            assertThat(value).isLessThan(LocalTime.parse("06:00"))
        }
    }
}

class InstantSource {

    fun example() {
        fun testInstantSource(
            @InstantSource(
                values = [
                    "2023-01-01T00:00:00.000Z",
                    "2022-01-01T00:00:00.000Z",
                    "2021-01-01T00:00:00.000Z"
                ]
            )
            value: Instant
        ) {
            assertThat(value).isLessThan(Instant.now())
        }
    }
}

class RandomInstantSource {

    fun example() {

        /**
         * This will generate 10 tests with random Instant values
         * between 2023-01-01T00:00:00.000Z and 2023-01-02T01:00:00.000Z
         */
        @GeneratedParametersTest
        fun testRandomInstantSourceWithInstant(
            @RandomInstantSource(
                size = 10,
                minInstant = "2023-01-01T00:00:00.000Z",
                maxInstant = "2023-01-02T01:00:00.000Z"
            )
            value: Instant
        ) {
            assertThat(value).isLessThan(Instant.now())
        }

        @GeneratedParametersTest
        fun testRandomInstantSourceWithOffset(
            @RandomInstantSource(
                size = 10,
                minOffset = "PT1H",
                maxOffset = "PT2H"
            )
            value: Instant
        ) {
            assertThat(value).isBetween(
                Instant.now().plus(1, ChronoUnit.HOURS),
                Instant.now().plus(2, ChronoUnit.HOURS)
            )
        }
    }
}

class LocalDateRangeSource {

    fun example() {

        /**
         * This will generate 10 tests with random LocalDate values
         * between 2023-01-01 and 2023-01-10
         */
        @GeneratedParametersTest
        fun testLocalDateRangeSource(
            @LocalDateRangeSource(
                min = "2023-01-01",
                max = "2023-01-10"
            )
            value: LocalDate
        ) {
            assertThat(value).isBetween(
                LocalDate.parse("2023-01-01"),
                LocalDate.parse("2023-01-10")
            )
        }

        /**
         * This will generate 10 tests with random LocalDate values
         * between 01/01/2023 and 01/10/2023
         * The values will be parsed using the format "MM/dd/yyyy"
         */
        @GeneratedParametersTest
        fun testLocalDateRangeSourceWithFormat(
            @LocalDateRangeSource(
                min = "01/01/2023",
                max = "01/10/2023",
                dateFormat = "MM/dd/yyyy"
            )
            value: LocalDate
        ) {
            assertThat(value).isBetween(
                LocalDate.parse("2023-01-01"),
                LocalDate.parse("2023-01-10")
            )
        }

        /**
         * This will generate 10 tests with random LocalDate values
         * between 01/01/2023 and 01/10/2023
         * The values will be parsed using the format "MM/dd/yyyy"
         * The values will be incremented by 2 days
         */
        @GeneratedParametersTest
        fun testLocalDateRangeSourceWithIncrement(
            @LocalDateRangeSource(
                min = "01/01/2023",
                max = "01/10/2023",
                dateFormat = "MM/dd/yyyy",
                increment = "P2d"
            )
            value: LocalDate
        ) {
            assertThat(value).isBetween(
                LocalDate.parse("2023-01-01"),
                LocalDate.parse("2023-01-10")
            )
        }
    }
}
class LocalDateTimeRangeSource {

    fun example() {

        /**
         * This will generate tests with random LocalDateTime values
         * between 2023-01-01 12:00 and 2023-01-10 19:00
         */
        @GeneratedParametersTest
        fun testLocalDateTimeRangeSource(
            @LocalDateTimeRangeSource(
                min = "2023-01-01 12:00",
                max = "2023-01-10 19:00"
            )
            value: LocalDateTime
        ) {
            assertThat(value).isBetween(
                LocalDateTime.parse("2023-01-01 12:00"),
                LocalDateTime.parse("2023-01-10 19:00")
            )
        }

        /**
         * This will generate tests with random LocalDateTime values
         * between 01/01/2023 12:00 and 01/10/2023 19:00
         * The values will be parsed using the format "MM/dd/yyyy HH:mm"
         */
        @GeneratedParametersTest
        fun testLocalDateTimeRangeSourceWithFormat(
            @LocalDateTimeRangeSource(
                min = "01/01/2023 12:00",
                max = "01/10/2023 19:00",
                dateTimeFormat = "MM/dd/yyyy HH:mm"
            )
            value: LocalDateTime
        ) {
            assertThat(value).isBetween(
                LocalDateTime .parse("2023-01-01 12:00"),
                LocalDateTime.parse("2023-01-10 19:00")
            )
        }

        /**
         * This will generate tests with random LocalDateTime values
         * between 01/01/2023 12:00 and 01/10/2023 19:00
         * The values will be parsed using the format "MM/dd/yyyy HH:mm"
         * The values will be incremented by 12 hours
         */
        @GeneratedParametersTest
        fun testLocalDateTimeRangeSourceWithIncrement(
            @LocalDateTimeRangeSource(
                min = "01/01/2023 12:00",
                max = "01/10/2023 19:00",
                dateTimeFormat = "MM/dd/yyyy HH:mm",
                increment = "PT12h"
            )
            value: LocalDateTime
        ) {
            assertThat(value).isBetween(
                LocalDateTime .parse("2023-01-01 12:00"),
                LocalDateTime.parse("2023-01-10 19:00")
            )
        }
    }
}

class InstantRangeSource {

    fun example() {

        /**
         * This will generate tests with random Instant values
         * between 2023-01-01T00:00:00.000Z and 2023-01-10T00:00:00.000Z
         * The values will be truncated to minutes
         */
        @GeneratedParametersTest
        fun testInstantRangeSourceWithInstant(
            @InstantRangeSource(
                minInstant = "2023-01-01T00:00:00.000Z",
                maxInstant = "2023-01-10T00:00:00.000Z",
                truncateTo = TruncateChronoUnit.MINUTES
            )
            value: Instant
        ) {
            assertThat(value).isBetween(
                Instant.parse("2023-01-01T00:00:00.000Z"),
                Instant.parse("2023-01-10T00:00:00.000Z")
            )
        }

        /**
         * This will generate tests with random Instant values
         * between 12 hours before and after NOW
         *
         * The values will be truncated to minutes
         */
        @GeneratedParametersTest
        fun testInstantRangeSourceWithOffset(
            @InstantRangeSource(
                minOffset = "PT-12H",
                maxOffset = "PT12H",
                truncateTo = TruncateChronoUnit.MINUTES
            )
            value: Instant
        ) {
            assertThat(value).isBetween(
                Instant.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES),
                Instant.now().plus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MINUTES)
            )
        }
    }
}

class LocalTimeRangeSource {

    fun example() {

        /**
         * This will generate tests with random LocalTime values
         * between 12:00 and 19:00 with 1 hour increments
         */
        @GeneratedParametersTest
        fun testLocalTimeRangeSource(
            @LocalTimeRangeSource(
                min = "12:00",
                max = "19:00"
            )
            value: LocalTime
        ) {
            assertThat(value).isBetween(
                LocalTime.parse("12:00"),
                LocalTime.parse("19:00")
            )
        }
    }
}
