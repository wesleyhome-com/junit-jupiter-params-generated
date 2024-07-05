package com.wesleyhome.test.jupiter.kotlin

import com.wesleyhome.test.jupiter.annotations.DoubleRangeSource
import com.wesleyhome.test.jupiter.annotations.DoubleSource
import com.wesleyhome.test.jupiter.annotations.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.FloatSource
import com.wesleyhome.test.jupiter.annotations.InstantRangeSource
import com.wesleyhome.test.jupiter.annotations.InstantSource
import com.wesleyhome.test.jupiter.annotations.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.IntSource
import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateSource
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeSource
import com.wesleyhome.test.jupiter.annotations.LocalTimeRangeSource
import com.wesleyhome.test.jupiter.annotations.LocalTimeSource
import com.wesleyhome.test.jupiter.annotations.LongRangeSource
import com.wesleyhome.test.jupiter.annotations.LongSource
import com.wesleyhome.test.jupiter.annotations.ParametersSource
import com.wesleyhome.test.jupiter.annotations.RandomInstantSource
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.provider.step
import com.wesleyhome.test.jupiter.provider.toLocalDate
import com.wesleyhome.test.jupiter.provider.toLocalDateTime
import com.wesleyhome.test.jupiter.provider.toLocalTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.params.ParameterizedTest
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Function

class AnnotationsTest {

    companion object {
        private const val BOOLEAN = "BOOLEAN"
        private const val ENUM = "ENUM"
        private const val BOOL_ENUM = "BOOL_ENUM"
        private const val STRING_SOURCE = "STRING_SOURCE"
        private const val INT_SOURCE = "INT_SOURCE"
        private const val INT_RANGE = "INT_RANGE"
        private const val INT_RANGE_STEP = "INT_RANGE_STEP"
        private const val INT_RANGE_STEP_DESCENDING = "INT_RANGE_STEP_NEGATIVE"
        private const val LONG_SOURCE = "LONG_SOURCE"
        private const val LONG_RANGE = "LONG_RANGE"
        private const val LONG_RANGE_STEP = "LONG_RANGE_STEP"
        private const val DOUBLE_SOURCE = "DOUBLE_SOURCE"
        private const val DOUBLE_RANGE = "DOUBLE_RANGE"
        private const val DOUBLE_RANGE_STEP = "DOUBLE_RANGE_STEP"
        private const val FLOAT_SOURCE = "FLOAT_SOURCE"
        private const val FLOAT_RANGE = "FLOAT_RANGE"
        private const val FLOAT_RANGE_STEP = "FLOAT_RANGE_STEP"
        private const val LOCAL_DATE_SOURCE = "LOCAL_DATE_SOURCE"
        private const val LOCAL_DATE_SOURCE_WITH_PATTERN = "LOCAL_DATE_SOURCE_WITH_PATTERN"
        private const val LOCAL_DATE_RANGE = "LOCAL_DATE_RANGE"
        private const val LOCAL_DATE_RANGE_STEP = "LOCAL_DATE_RANGE_STEP"
        private const val LOCAL_DATE_RANGE_WITH_PATTERN = "LOCAL_DATE_RANGE_WITH_PATTERN"
        private const val LOCAL_DATE_RANGE_STEP_WITH_PATTERN = "LOCAL_DATE_RANGE_STEP_WITH_PATTERN"
        private const val LOCAL_DATE_TIME_SOURCE = "LOCAL_DATE_TIME_SOURCE"
        private const val LOCAL_DATE_TIME_SOURCE_WITH_PATTERN = "LOCAL_DATE_TIME_SOURCE_WITH_PATTERN"
        private const val LOCAL_DATE_TIME_RANGE = "LOCAL_DATE_TIME_RANGE"
        private const val LOCAL_DATE_TIME_RANGE_STEP = "LOCAL_DATE_TIME_RANGE_STEP"
        private const val LOCAL_DATE_TIME_RANGE_WITH_PATTERN = "LOCAL_DATE_TIME_RANGE_WITH_PATTERN"
        private const val LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN = "LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN"

        private const val LOCAL_TIME_SOURCE: String = "LOCAL_TIME_SOURCE"
        private const val LOCAL_TIME_SOURCE_WITH_PATTERN: String = "LOCAL_TIME_SOURCE_WITH_PATTERN"
        private const val LOCAL_TIME_RANGE: String = "LOCAL_TIME_RANGE"
        private const val LOCAL_TIME_RANGE_STEP: String = "LOCAL_TIME_RANGE_STEP"
        private const val LOCAL_TIME_RANGE_WITH_PATTERN: String = "LOCAL_TIME_RANGE_WITH_PATTERN"
        private const val LOCAL_TIME_RANGE_STEP_WITH_PATTERN: String = "LOCAL_TIME_RANGE_STEP_WITH_PATTERN"
        private const val INSTANT_VALUE_SOURCE: String = "INSTANT_VALUE_SOURCE"
        private const val RANDOM_INSTANT_VALUE_SOURCE: String = "RANDOM_INSTANT_VALUE_SOURCE"
        private const val RANDOM_INSTANT_VALUE_OFFSET_SOURCE: String = "RANDOM_INSTANT_VALUE_OFFSET_SOURCE"
        private const val INSTANT_RANGE_SOURCE: String = "INSTANT_RANGE_SOURCE"
        private const val INSTANT_RANGE_OFFSET_SOURCE: String = "INSTANT_RANGE_OFFSET_SOURCE"

        private val stringMap = mutableMapOf<String, AtomicReference<List<String>>>()
        private val intMap = mutableMapOf<String, AtomicInteger>()

        fun append(map: String, value: Any) {
            ref(map).updateAndGet {
                it + value.toString()
            }
        }

        fun update(map: String, block: (Int) -> Int) {
            int(map).updateAndGet(block)
        }

        fun getRef(map: String): List<String> = ref(map).get()

        private fun ref(map: String): AtomicReference<List<String>> = stringMap.computeIfAbsent(map) {
            AtomicReference(mutableListOf())
        }

        private fun int(map: String): AtomicInteger = intMap.computeIfAbsent(map) {
            AtomicInteger(0)
        }

        private fun typeArray(range: IntProgression) = range.toList().map { "$it" }.toTypedArray()

        /**
         * Unorthodox way, until I can come up with something better to validate
         * the parameters being generated from the annotations
         */
        @AfterAll
        @JvmStatic
        fun tester() {
            assertThat(getRef(BOOLEAN)).containsExactlyInAnyOrder("true", "false")
            assertThat(getRef(ENUM)).containsOnly(*TestKotlinEnum.entries.map { it.name }.toTypedArray())
            val map = TestKotlinEnum.entries.flatMap { enum ->
                listOf(true, false).map { bool ->
                    "$bool,$enum"
                }
            }
            assertThat(getRef(BOOL_ENUM)).containsOnly(*map.toTypedArray())
            assertThat(int(INT_SOURCE)).hasValue(60)
            assertThat(getRef(INT_RANGE)).containsExactly(*typeArray((10..20)))
            assertThat(getRef(INT_RANGE_STEP))
                .containsExactly(*typeArray((10..20 step 5)))
            assertThat(getRef(INT_RANGE_STEP_DESCENDING))
                .containsExactly(*typeArray((20 downTo 10 step 5)))
            assertThat(int(LONG_SOURCE)).hasValue(60)
            assertThat(int(LONG_RANGE)).hasValue((10..20).sum())
            assertThat(int(LONG_RANGE_STEP)).hasValue((10..20 step 5).sum())
            assertThat(getRef(DOUBLE_SOURCE)).containsOnly("10.0", "20.0", "30.0")
            assertThat(getRef(DOUBLE_RANGE)).describedAs(DOUBLE_RANGE)
                .containsOnly(*((10.0..20.0 step .5).toList().map { "$it" }.toTypedArray()))
            assertThat(getRef(DOUBLE_RANGE_STEP)).describedAs(DOUBLE_RANGE_STEP)
                .containsOnly(*(10.0..20.0 step 1.5).toList().map { "$it" }.toTypedArray())
            assertThat(getRef(FLOAT_SOURCE)).containsOnly("10.0", "20.0", "30.0")
            assertThat(getRef(FLOAT_RANGE)).describedAs(FLOAT_RANGE)
                .containsOnly(*((10.0..20.0 step .5).toList().map { "$it" }.toTypedArray()))
            assertThat(getRef(FLOAT_RANGE_STEP)).describedAs(FLOAT_RANGE_STEP)
                .containsOnly(*(10.0..20.0 step 1.5).toList().map { "$it" }.toTypedArray())
            assertThat(getRef(LOCAL_DATE_SOURCE)).containsOnly("2022-01-01", "2023-01-01", "2023-02-01")
            assertThat(getRef(LOCAL_DATE_SOURCE_WITH_PATTERN)).containsOnly("2022-01-01", "2023-01-01", "2023-02-01")
            assertThat(getRef(LOCAL_DATE_RANGE)).describedAs(LOCAL_DATE_RANGE)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01".toLocalDate().."2022-01-31".toLocalDate() step "P1d").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_RANGE_STEP)).describedAs(LOCAL_DATE_RANGE_STEP)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01".toLocalDate().."2022-01-31".toLocalDate() step "P5d").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_RANGE_WITH_PATTERN)).describedAs(LOCAL_DATE_RANGE_WITH_PATTERN)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01".toLocalDate().."2022-01-31".toLocalDate() step "P1d").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_RANGE_STEP_WITH_PATTERN)).describedAs(LOCAL_DATE_RANGE_STEP_WITH_PATTERN)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01".toLocalDate().."2022-01-31".toLocalDate() step "P5d").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_TIME_SOURCE)).describedAs(LOCAL_DATE_TIME_SOURCE)
                .containsOnly("2022-01-01T12:30", "2022-01-01T13:30")
            assertThat(getRef(LOCAL_DATE_TIME_SOURCE_WITH_PATTERN)).describedAs(LOCAL_DATE_TIME_SOURCE_WITH_PATTERN)
                .containsOnly("2022-01-01T00:30", "2022-01-01T13:30")
            assertThat(getRef(LOCAL_DATE_TIME_RANGE)).describedAs(LOCAL_DATE_TIME_RANGE)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01 12:30".toLocalDateTime().."2022-01-01 19:30".toLocalDateTime() step "PT1h").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_TIME_RANGE_STEP)).describedAs(LOCAL_DATE_TIME_RANGE_STEP)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01 12:30".toLocalDateTime().."2022-01-01 19:30".toLocalDateTime() step "PT30m").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_TIME_RANGE_WITH_PATTERN)).describedAs(LOCAL_DATE_TIME_RANGE_WITH_PATTERN)
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01 12:30".toLocalDateTime().."2022-01-01 19:30".toLocalDateTime() step "PT1h").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN)).describedAs(
                LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN
            )
                .containsExactlyInAnyOrderElementsOf(
                    ("2022-01-01 12:30".toLocalDateTime().."2022-01-01 19:30".toLocalDateTime() step "PT30m").map {
                        "$it"
                    }.toList()
                )
            assertThat(getRef(STRING_SOURCE))
                .describedAs(STRING_SOURCE)
                .containsExactlyInAnyOrder("Ten", "Twenty", "Thirty")
            assertThat(getRef(LOCAL_TIME_SOURCE)).describedAs(
                LOCAL_TIME_SOURCE
            )
                .containsOnly("12:30", "12:30:01")
            assertThat(getRef(LOCAL_TIME_SOURCE_WITH_PATTERN)).describedAs(
                LOCAL_TIME_SOURCE_WITH_PATTERN
            )
                .containsOnly("12:30", "12:31")
            val localTimeRange =
                ("12:00".toLocalTime().."18:00".toLocalTime() step "PT1H")
                    .map { it.toString() }
            assertThat(getRef(LOCAL_TIME_RANGE)).describedAs(LOCAL_TIME_RANGE)
                .containsExactlyInAnyOrderElementsOf(localTimeRange)
            val localTimeRangeStep = ("12:00".toLocalTime().."13:00".toLocalTime() step "PT1m")
                .map { it.toString() }
            assertThat(getRef(LOCAL_TIME_RANGE_STEP)).describedAs(
                LOCAL_TIME_RANGE_STEP
            ).containsExactlyInAnyOrderElementsOf(localTimeRangeStep)

            assertThat(getRef(LOCAL_TIME_RANGE_WITH_PATTERN)).describedAs(
                LOCAL_TIME_RANGE_WITH_PATTERN
            ).containsExactlyInAnyOrderElementsOf(localTimeRange)

            assertThat(getRef(LOCAL_TIME_RANGE_STEP_WITH_PATTERN)).describedAs(
                LOCAL_TIME_RANGE_STEP_WITH_PATTERN
            ).containsExactlyInAnyOrderElementsOf(localTimeRangeStep)

            // "2024-01-01T12:30:00Z", "2024-01-01T13:30:00Z"
            val instantRangeList = listOf(
                ZonedDateTime.of(2024, 1, 1, 12, 30, 0, 0, ZoneId.of("UTC")),
                ZonedDateTime.of(2024, 1, 1, 13, 30, 0, 0, ZoneId.of("UTC"))
            )
                .map { it.toInstant() }
            val instantValueSource = instantRangeList
                .map { it.toString() }
            assertThat(getRef(INSTANT_VALUE_SOURCE)).describedAs(INSTANT_VALUE_SOURCE)
                .containsExactlyInAnyOrderElementsOf(instantValueSource)
            val instantRange = instantRangeList[0] .. instantRangeList[1]
            assertThat(getRef(RANDOM_INSTANT_VALUE_SOURCE)).describedAs(RANDOM_INSTANT_VALUE_SOURCE)
                .hasSize(10)
                .map(Function {
                    Instant.parse(it)
                })
                .allMatch { it in instantRange }
            val now = ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS).toInstant()
            val start = now.minus(1, ChronoUnit.HOURS)
            val end = now.plus(2, ChronoUnit.DAYS)
            val instantOffsetRange = start..end
            assertThat(getRef(RANDOM_INSTANT_VALUE_OFFSET_SOURCE)).describedAs(RANDOM_INSTANT_VALUE_OFFSET_SOURCE)
                .hasSize(10)
                .map(Function {
                    Instant.parse(it)
                })
                .allMatch { it in instantOffsetRange }

            val startInstant = ZonedDateTime.of(2024, 1, 1, 12, 30, 0, 0, ZoneId.of("UTC")).toInstant()
            val endInstant = ZonedDateTime.of(2024, 1, 2, 12, 30, 0, 0, ZoneId.of("UTC")).toInstant()
            val expectedValues = (startInstant..endInstant step "PT1h").map { it.toString() }.toList()
            assertThat(getRef(INSTANT_RANGE_SOURCE)).describedAs(INSTANT_RANGE_SOURCE)
                .containsExactlyInAnyOrderElementsOf(expectedValues)
            val startOffsetInstant = now.minus(2, ChronoUnit.DAYS)
            val endOffsetInstant = now.plus(12, ChronoUnit.HOURS)
            val expectedOffsetValues = (startOffsetInstant..endOffsetInstant step "PT6h").map { it.toString() }.toList()
            assertThat(getRef(INSTANT_RANGE_OFFSET_SOURCE)).describedAs(INSTANT_RANGE_OFFSET_SOURCE)
                .containsExactlyInAnyOrderElementsOf(expectedOffsetValues)
        }
    }

    @ParameterizedTest
    @ParametersSource
    fun testStringSource(
        @StringSource(values = ["Ten", "Twenty", "Thirty"]) value: String
    ) {
        append(STRING_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testBoolean(enabled: Boolean) {
        append(BOOLEAN, enabled.toString())
    }

    @ParameterizedTest
    @ParametersSource
    fun testEnum(enumValue: TestKotlinEnum) {
        append(ENUM, enumValue.name)
    }

    @ParameterizedTest
    @ParametersSource
    fun testBoolEnum(enabled: Boolean, enumValue: TestKotlinEnum) {
        append(BOOL_ENUM, "$enabled,$enumValue")
    }

    @ParameterizedTest
    @ParametersSource
    fun testIntSource(@IntSource(values = [10, 20, 30]) value: Int) {
        update(INT_SOURCE) {
            it + value
        }
    }

    @ParameterizedTest
    @ParametersSource
    fun testIntRange(@IntRangeSource(min = 10, max = 20) value: Int) {
        append(INT_RANGE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testIntRangeStep(@IntRangeSource(min = 10, max = 20, increment = 5) value: Int) {
        append(INT_RANGE_STEP, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testIntRangeStepDescending(@IntRangeSource(min = 10, max = 20, increment = 5, ascending = false) value: Int) {
        append(INT_RANGE_STEP_DESCENDING, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLongSource(@LongSource(values = [10, 20, 30]) value: Long) {
        update(LONG_SOURCE) {
            it + value.toInt()
        }
    }

    @ParameterizedTest
    @ParametersSource
    fun testLongRange(@LongRangeSource(min = 10, max = 20) value: Long) {
        update(LONG_RANGE) {
            it + value.toInt()
        }
    }

    @ParameterizedTest
    @ParametersSource
    fun testLongRangeStep(@LongRangeSource(min = 10, max = 20, increment = 5) value: Long) {
        update(LONG_RANGE_STEP) {
            it + value.toInt()
        }
    }

    @ParameterizedTest
    @ParametersSource
    fun testDoubleSource(@DoubleSource(values = [10.0, 20.0, 30.0]) value: Double) {
        append(DOUBLE_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testDoubleRange(@DoubleRangeSource(min = 10.0, max = 20.0) value: Double) {
        append(DOUBLE_RANGE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testDoubleRangeStep(@DoubleRangeSource(min = 10.0, max = 20.0, increment = 1.5) value: Double) {
        append(DOUBLE_RANGE_STEP, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testFloatSource(@FloatSource(values = [10.0f, 20.0f, 30.0f]) value: Float) {
        append(FLOAT_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testFloatRange(@FloatRangeSource(min = 10.0f, max = 20.0f) value: Float) {
        append(FLOAT_RANGE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testFloatRangeStep(@FloatRangeSource(min = 10.0f, max = 20.0f, increment = 1.5f) value: Float) {
        append(FLOAT_RANGE_STEP, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateSource(@LocalDateSource(values = ["2022-01-01", "2023-01-01", "2023-02-01"]) value: LocalDate) {
        append(LOCAL_DATE_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateSourceWithPattern(
        @LocalDateSource(
            values = ["01/01/2022", "01/01/2023", "02/01/2023"],
            dateFormat = "MM/dd/yyyy"
        ) value: LocalDate
    ) {
        append(LOCAL_DATE_SOURCE_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateRange(@LocalDateRangeSource(min = "2022-01-01", max = "2022-01-31") value: LocalDate) {
        append(LOCAL_DATE_RANGE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateRangeStep(
        @LocalDateRangeSource(
            min = "2022-01-01",
            max = "2022-01-31",
            increment = "P5d"
        ) value: LocalDate
    ) {
        append(LOCAL_DATE_RANGE_STEP, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateRangeWithPattern(
        @LocalDateRangeSource(
            min = "01/01/2022",
            max = "01/31/2022",
            dateFormat = "MM/dd/yyyy"
        ) value: LocalDate
    ) {
        append(LOCAL_DATE_RANGE_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateRangeStepWithPattern(
        @LocalDateRangeSource(
            min = "01/01/2022",
            max = "01/31/2022",
            increment = "P5d",
            dateFormat = "MM/dd/yyyy"
        ) value: LocalDate
    ) {
        append(LOCAL_DATE_RANGE_STEP_WITH_PATTERN, value)
    }


    @ParameterizedTest
    @ParametersSource
    fun testLocalDateTimeSource(@LocalDateTimeSource(values = ["2022-01-01 12:30", "2022-01-01 13:30"]) value: LocalDateTime) {
        append(LOCAL_DATE_TIME_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateTimeSourceWithPattern(
        @LocalDateTimeSource(
            values = ["01/01/2022 12:30 AM", "01/01/2022 1:30 PM"],
            dateTimeFormat = "MM/dd/yyyy h:mm a"
        ) value: LocalDateTime
    ) {
        append(LOCAL_DATE_TIME_SOURCE_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateTimeRange(
        @LocalDateTimeRangeSource(
            min = "2022-01-01 12:30",
            max = "2022-01-01 19:30",
        ) value: LocalDateTime
    ) {
        append(LOCAL_DATE_TIME_RANGE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateTimeRangeStep(
        @LocalDateTimeRangeSource(
            min = "2022-01-01 12:30",
            max = "2022-01-01 19:30",
            increment = "PT30m"
        ) value: LocalDateTime
    ) {
        append(LOCAL_DATE_TIME_RANGE_STEP, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateTimeRangeWithPattern(
        @LocalDateTimeRangeSource(
            min = "01/01/2022 12:30",
            max = "01/01/2022 19:30",
            dateTimeFormat = "MM/dd/yyyy HH:mm"
        ) value: LocalDateTime
    ) {
        append(LOCAL_DATE_TIME_RANGE_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalDateTimeRangeStepWithPattern(
        @LocalDateTimeRangeSource(
            min = "01/01/2022 12:30",
            max = "01/01/2022 19:30",
            increment = "PT30m",
            dateTimeFormat = "MM/dd/yyyy HH:mm"
        ) value: LocalDateTime
    ) {
        append(LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN, value)
    }


    @ParameterizedTest
    @ParametersSource
    fun testLocalTimeSource(
        @LocalTimeSource(
            values = ["12:30:00", "12:30:01"],
            timeFormat = "HH:mm:ss"
        ) value: LocalTime
    ) {
        append(LOCAL_TIME_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalTimeSourceWithPattern(@LocalTimeSource(values = ["12:30", "12:31"]) value: LocalTime) {
        append(LOCAL_TIME_SOURCE_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalTimeRange(@LocalTimeRangeSource(min = "12:00", max = "18:00") value: LocalTime) {
        append(LOCAL_TIME_RANGE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalTimeRangeWithPattern(
        @LocalTimeRangeSource(
            min = "12:00:00",
            max = "18:00:00",
            timeFormat = "HH:mm:ss"
        ) value: LocalTime
    ) {
        append(LOCAL_TIME_RANGE_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalTimeRangeStep(
        @LocalTimeRangeSource(
            min = "12:00",
            max = "13:00",
            increment = "PT1m"
        ) value: LocalTime
    ) {
        append(LOCAL_TIME_RANGE_STEP, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testLocalTimeRangeStepWithPattern(
        @LocalTimeRangeSource(
            min = "12:00:00",
            max = "13:00:00",
            increment = "PT1m",
            timeFormat = "HH:mm:ss"
        ) value: LocalTime
    ) {
        append(LOCAL_TIME_RANGE_STEP_WITH_PATTERN, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testInstantValueSource(@InstantSource(values = ["2024-01-01T12:30:00Z", "2024-01-01T13:30:00Z"]) value: Instant) {
        append(INSTANT_VALUE_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testRandomInstantSource(
        @RandomInstantSource(
            size = 10,
            minInstant = "2024-01-01T12:30:00Z",
            maxInstant = "2024-01-01T13:30:00Z"
        )
        value: Instant
    ) {
        append(RANDOM_INSTANT_VALUE_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testRandomInstantSourceWithOffset (
        @RandomInstantSource(
            size = 10,
            truncateTo = "HOURS",
            minOffset = "-PT1H",
            maxOffset = "P2D"
        )
        value: Instant
    ) {
        append(RANDOM_INSTANT_VALUE_OFFSET_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testInstantRangeSource(
        @InstantRangeSource(
            minInstant = "2024-01-01T12:30:00Z",
            maxInstant = "2024-01-02T12:30:00Z",
        )
        value: Instant
    ) {
        append(INSTANT_RANGE_SOURCE, value)
    }

    @ParameterizedTest
    @ParametersSource
    fun testInstantRangeSourceWithOffset(
        @InstantRangeSource(
            minOffset = "-P2D",
            maxOffset = "PT12h",
            truncateTo = "HOURS",
            increment = "PT6h",
        )
        value: Instant
    ) {
        append(INSTANT_RANGE_OFFSET_SOURCE, value)
    }
}
