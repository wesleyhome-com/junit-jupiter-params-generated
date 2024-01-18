package com.wesleyhome.test.jupiter.provider;

import com.wesleyhome.test.jupiter.annotations.DoubleRangeSource;
import com.wesleyhome.test.jupiter.annotations.DoubleSource;
import com.wesleyhome.test.jupiter.annotations.FloatRangeSource;
import com.wesleyhome.test.jupiter.annotations.FloatSource;
import com.wesleyhome.test.jupiter.annotations.IntRangeSource;
import com.wesleyhome.test.jupiter.annotations.IntSource;
import com.wesleyhome.test.jupiter.annotations.LocalDateRangeSource;
import com.wesleyhome.test.jupiter.annotations.LocalDateSource;
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeRangeSource;
import com.wesleyhome.test.jupiter.annotations.LocalDateTimeSource;
import com.wesleyhome.test.jupiter.annotations.LongRangeSource;
import com.wesleyhome.test.jupiter.annotations.LongSource;
import com.wesleyhome.test.jupiter.annotations.ParametersSource;
import com.wesleyhome.test.jupiter.kotlin.TestKotlinEnum;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntUnaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnnotationsTestJava {

    private static final String BOOLEAN = "BOOLEAN";
    private static final String ENUM = "ENUM";
    private static final String BOOL_ENUM = "BOOL_ENUM";
    private static final String INT_SOURCE = "INT_SOURCE";
    private static final String INTEGER_SOURCE = "INTEGER_SOURCE";
    private static final String INT_RANGE = "INT_RANGE";
    private static final String INT_RANGE_STEP = "INT_RANGE_STEP";
    private static final String LONG_SOURCE = "LONG_SOURCE";
    private static final String LONG_RANGE = "LONG_RANGE";
    private static final String LONG_RANGE_STEP = "LONG_RANGE_STEP";
    private static final String DOUBLE_SOURCE = "DOUBLE_SOURCE";
    private static final String DOUBLE_RANGE = "DOUBLE_RANGE";
    private static final String DOUBLE_RANGE_STEP = "DOUBLE_RANGE_STEP";
    private static final String FLOAT_SOURCE = "FLOAT_SOURCE";
    private static final String FLOAT_RANGE = "FLOAT_RANGE";
    private static final String FLOAT_RANGE_STEP = "FLOAT_RANGE_STEP";
    private static final String LOCAL_DATE_SOURCE = "LOCAL_DATE_SOURCE";
    private static final String LOCAL_DATE_SOURCE_WITH_PATTERN = "LOCAL_DATE_SOURCE_WITH_PATTERN";
    private static final String LOCAL_DATE_RANGE = "LOCAL_DATE_RANGE";
    private static final String LOCAL_DATE_RANGE_STEP = "LOCAL_DATE_RANGE_STEP";
    private static final String LOCAL_DATE_RANGE_WITH_PATTERN = "LOCAL_DATE_RANGE_WITH_PATTERN";
    private static final String LOCAL_DATE_RANGE_STEP_WITH_PATTERN = "LOCAL_DATE_RANGE_STEP_WITH_PATTERN";
    private static final String LOCAL_DATE_TIME_SOURCE = "LOCAL_DATE_TIME_SOURCE";
    private static final String LOCAL_DATE_TIME_SOURCE_WITH_PATTERN = "LOCAL_DATE_TIME_SOURCE_WITH_PATTERN";
    private static final String LOCAL_DATE_TIME_RANGE = "LOCAL_DATE_TIME_RANGE";
    private static final String LOCAL_DATE_TIME_RANGE_STEP = "LOCAL_DATE_TIME_RANGE_STEP";
    private static final String LOCAL_DATE_TIME_RANGE_WITH_PATTERN = "LOCAL_DATE_TIME_RANGE_WITH_PATTERN";
    private static final String LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN = "LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN";

    private static final Map<String, AtomicReference<List<String>>> stringMap = new HashMap<>();
    private static final Map<String, AtomicInteger> intMap = new HashMap<>();

    private static void append(String map, Object value) {
        ref(map).updateAndGet(list -> {
            var l = new ArrayList<>(list);
            l.add(value.toString());
            return l;
        });
    }

    private static int update(String map, IntUnaryOperator block) {
        return intMap(map).updateAndGet(block);
    }

    private static List<String> getRef(String map) {
        return ref(map).get();
    }

    private static AtomicReference<List<String>> ref(String map) {
        return stringMap.computeIfAbsent(map, m -> new AtomicReference(new ArrayList<>()));
    }

    private static AtomicInteger intMap(String map) {
        return intMap.computeIfAbsent(map, key -> new AtomicInteger());
    }

    @AfterAll
    static void tester() {
        assertThat(getRef(BOOLEAN)).describedAs(BOOLEAN).containsExactlyInAnyOrder("true", "false");
        assertThat(getRef(ENUM)).describedAs(ENUM).containsExactlyInAnyOrderElementsOf(
            Arrays.stream(TestKotlinEnum.values()).map(it -> it.name()).collect(toList()));
        var map = Arrays.stream(TestKotlinEnum.values())
            .flatMap(it -> Stream.of(true, false).map(bool -> String.format("%s,%s", bool, it)))
            .collect(toList());
        assertThat(getRef(BOOL_ENUM)).describedAs(BOOL_ENUM).containsExactlyInAnyOrderElementsOf(map);
        assertThat(intMap(INT_SOURCE)).describedAs(INT_SOURCE).hasValue(60);
        assertThat(intMap(INTEGER_SOURCE)).describedAs(INT_SOURCE).hasValue(60);
        final var tenToTwentySum = IntStream.rangeClosed(10, 20).sum();
        assertThat(intMap(INT_RANGE)).describedAs(INT_RANGE).hasValue(tenToTwentySum);
        final var tenToTwentyMultipleOf5Sum = IntStream.rangeClosed(10, 20).filter(i -> i % 5 == 0).sum();
        assertThat(intMap(INT_RANGE_STEP)).describedAs(INT_RANGE_STEP).hasValue(tenToTwentyMultipleOf5Sum);
        assertThat(intMap(LONG_SOURCE)).describedAs(LONG_SOURCE).hasValue(60);
        assertThat(intMap(LONG_RANGE)).describedAs(LONG_RANGE).hasValue(tenToTwentySum);
        assertThat(intMap(LONG_RANGE_STEP)).describedAs(LONG_RANGE_STEP).hasValue(tenToTwentyMultipleOf5Sum);
        assertThat(getRef(DOUBLE_SOURCE)).describedAs(DOUBLE_SOURCE).containsOnly("10.0", "20.0", "30.0");
        final var doubleStreamStepPoint5 = DoubleStream.iterate(10d, d -> d <= 20d, d -> d + .5)
            .boxed()
            .map(Object::toString)
            .collect(toList());
        final var doubleStreamStepOnePoint5 = DoubleStream.iterate(10d, d -> d <= 20d, d -> d + 1.5)
            .boxed()
            .map(Object::toString)
            .collect(toList());
        assertThat(getRef(DOUBLE_RANGE)).describedAs(DOUBLE_RANGE)
            .containsOnlyOnceElementsOf(doubleStreamStepPoint5);
        assertThat(getRef(DOUBLE_RANGE_STEP)).describedAs(DOUBLE_RANGE_STEP)
            .containsOnlyOnceElementsOf(doubleStreamStepOnePoint5);
        assertThat(getRef(FLOAT_SOURCE)).containsOnly("10.0", "20.0", "30.0");
        assertThat(getRef(FLOAT_RANGE)).describedAs(FLOAT_RANGE)
            .containsOnlyOnceElementsOf(doubleStreamStepPoint5);
        assertThat(getRef(FLOAT_RANGE_STEP)).describedAs(FLOAT_RANGE_STEP)
            .containsOnlyOnceElementsOf(doubleStreamStepOnePoint5);
        assertThat(getRef(LOCAL_DATE_SOURCE)).containsOnly("2022-01-01", "2023-01-01", "2023-02-01");
        assertThat(getRef(LOCAL_DATE_SOURCE_WITH_PATTERN)).containsOnly("2022-01-01", "2023-01-01", "2023-02-01");

        final var dateRange = Stream.iterate(
            LocalDate.parse("2022-01-01"),
            d -> d.isBefore(LocalDate.parse("2022-02-01")),
            d -> d.plusDays(1)
        ).map(Object::toString).collect(toList());
        final var filteredDateRange = Stream.iterate(
            LocalDate.parse("2022-01-01"),
            d -> d.isBefore(LocalDate.parse("2022-02-01")),
            d -> d.plusDays(5)
        ).map(Object::toString).collect(toList());
        assertThat(getRef(LOCAL_DATE_RANGE)).describedAs(LOCAL_DATE_RANGE)
            .containsExactlyInAnyOrderElementsOf(dateRange);
        assertThat(getRef(LOCAL_DATE_RANGE_STEP)).describedAs(LOCAL_DATE_RANGE_STEP)
            .containsExactlyInAnyOrderElementsOf(filteredDateRange);
        assertThat(getRef(LOCAL_DATE_RANGE_WITH_PATTERN)).describedAs(LOCAL_DATE_RANGE_WITH_PATTERN)
            .containsExactlyInAnyOrderElementsOf(dateRange);
        assertThat(getRef(LOCAL_DATE_RANGE_STEP_WITH_PATTERN)).describedAs(LOCAL_DATE_RANGE_STEP_WITH_PATTERN)
            .containsExactlyInAnyOrderElementsOf(filteredDateRange);
        final var dateTimeRange = Stream.iterate(
            LocalDateTime.parse("2022-01-01T12:30"),
            dt -> dt.isBefore(LocalDateTime.parse("2022-01-01T19:31")),
            dt -> dt.plusHours(1)
        ).map(Object::toString).collect(toList());
        final var filteredDateTimeRange = Stream.iterate(
            LocalDateTime.parse("2022-01-01T12:30"),
            dt -> dt.isBefore(LocalDateTime.parse("2022-01-01T19:31")),
            dt -> dt.plusMinutes(30)
        ).map(Object::toString).collect(toList());
        assertThat(getRef(LOCAL_DATE_TIME_SOURCE)).describedAs(LOCAL_DATE_TIME_SOURCE)
            .containsOnly("2022-01-01T12:30", "2022-01-01T13:30");
        assertThat(getRef(LOCAL_DATE_TIME_SOURCE_WITH_PATTERN)).describedAs(LOCAL_DATE_TIME_SOURCE_WITH_PATTERN)
            .containsOnly("2022-01-01T00:30", "2022-01-01T13:30");
        assertThat(getRef(LOCAL_DATE_TIME_RANGE)).describedAs(LOCAL_DATE_TIME_RANGE)
            .containsExactlyInAnyOrderElementsOf(dateTimeRange);
        assertThat(getRef(LOCAL_DATE_TIME_RANGE_STEP)).describedAs(LOCAL_DATE_TIME_RANGE_STEP)
            .containsExactlyInAnyOrderElementsOf(filteredDateTimeRange);
        assertThat(getRef(LOCAL_DATE_TIME_RANGE_WITH_PATTERN)).describedAs(LOCAL_DATE_TIME_RANGE_WITH_PATTERN)
            .containsExactlyInAnyOrderElementsOf(dateTimeRange);
        assertThat(getRef(LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN)).describedAs(LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN)
            .containsExactlyInAnyOrderElementsOf(filteredDateTimeRange);
    }

    @ParameterizedTest
    @ParametersSource
    void testBoolean(boolean enabled) {
        System.out.println(enabled);
        append(BOOLEAN, enabled);
    }

    @ParameterizedTest
    @ParametersSource
    void testEnum(TestKotlinEnum enumValue) {
        append(ENUM, enumValue.name());
    }

    @ParameterizedTest
    @ParametersSource
    void testBoolEnum(Boolean enabled, TestKotlinEnum enumValue) {
        append(BOOL_ENUM, String.format("%s,%s", enabled, enumValue));
    }

    @ParameterizedTest
    @ParametersSource
    void testIntSource(@IntSource(values = {10, 20, 30}) int value) {
        System.out.println(value);
        update(INT_SOURCE, it -> it + value);
    }

    @ParameterizedTest
    @ParametersSource
    void testIntegerSource(@IntSource(values = {10, 20, 30}) Integer value) {
        System.out.println(value);
        update(INTEGER_SOURCE, it -> it + value);
    }

    @ParameterizedTest
    @ParametersSource
    void testIntRange(@IntRangeSource(min = 10, max = 20) int value) {
        update(INT_RANGE, it -> it + value);
    }

    @ParameterizedTest
    @ParametersSource
    void testIntRangeStep(@IntRangeSource(min = 10, max = 20, increment = 5) int value) {
        update(INT_RANGE_STEP, it -> it + value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLongSource(@LongSource(values = {10, 20, 30}) long value) {
        update(LONG_SOURCE, it -> it + (int) value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLongRange(@LongRangeSource(min = 10, max = 20) long value) {
        update(LONG_RANGE, it -> it + (int) value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLongRangeStep(@LongRangeSource(min = 10, max = 20, increment = 5) long value) {
        update(LONG_RANGE_STEP, it -> it + (int) value);
    }

    @ParameterizedTest
    @ParametersSource
    void testDoubleSource(@DoubleSource(values = {10.0, 20.0, 30.0}) double value) {
        append(DOUBLE_SOURCE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testDoubleRange(@DoubleRangeSource(min = 10.0, max = 20.0) double value) {
        append(DOUBLE_RANGE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testDoubleRangeStep(@DoubleRangeSource(min = 10.0, max = 20.0, increment = 1.5) double value) {
        append(DOUBLE_RANGE_STEP, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testFloatSource(@FloatSource(values = {10.0f, 20.0f, 30.0f}) Float value) {
        append(FLOAT_SOURCE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testFloatRange(@FloatRangeSource(min = 10.0f, max = 20.0f) Float value) {
        append(FLOAT_RANGE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testFloatRangeStep(@FloatRangeSource(min = 10.0f, max = 20.0f, increment = 1.5f) Float value) {
        append(FLOAT_RANGE_STEP, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateSource(@LocalDateSource(values = {"2022-01-01", "2023-01-01", "2023-02-01"}) LocalDate value) {
        append(LOCAL_DATE_SOURCE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateSourceWithPattern(
        @LocalDateSource(
            values = {"01/01/2022", "01/01/2023", "02/01/2023"},
            dateFormat = "MM/dd/yyyy"
        ) LocalDate value
    ) {
        append(LOCAL_DATE_SOURCE_WITH_PATTERN, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateRange(@LocalDateRangeSource(min = "2022-01-01", max = "2022-01-31") LocalDate value) {
        append(LOCAL_DATE_RANGE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateRangeStep(
        @LocalDateRangeSource(
            min = "2022-01-01",
            max = "2022-01-31",
            increment = "P5d"
        ) LocalDate value
    ) {
        append(LOCAL_DATE_RANGE_STEP, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateRangeWithPattern(
        @LocalDateRangeSource(
            min = "01/01/2022",
            max = "01/31/2022",
            dateFormat = "MM/dd/yyyy"
        ) LocalDate value
    ) {
        append(LOCAL_DATE_RANGE_WITH_PATTERN, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateRangeStepWithPattern(
        @LocalDateRangeSource(
            min = "01/01/2022",
            max = "01/31/2022",
            increment = "P5d",
            dateFormat = "MM/dd/yyyy"
        ) LocalDate value
    ) {
        append(LOCAL_DATE_RANGE_STEP_WITH_PATTERN, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateTimeSource(
        @LocalDateTimeSource(values = {"2022-01-01 12:30", "2022-01-01 13:30"})
        LocalDateTime value
    ) {
        append(LOCAL_DATE_TIME_SOURCE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateTimeSourceWithPattern(
        @LocalDateTimeSource(
            values = {"01/01/2022 12:30 AM", "01/01/2022 1:30 PM"},
            dateTimeFormat = "MM/dd/yyyy h:mm a"
        ) LocalDateTime value
    ) {
        append(LOCAL_DATE_TIME_SOURCE_WITH_PATTERN, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateTimeRange(
        @LocalDateTimeRangeSource(
            min = "2022-01-01 12:30",
            max = "2022-01-01 19:30"
        ) LocalDateTime value
    ) {
        append(LOCAL_DATE_TIME_RANGE, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateTimeRangeStep(
        @LocalDateTimeRangeSource(
            min = "2022-01-01 12:30",
            max = "2022-01-01 19:30",
            increment = "PT30m"
        ) LocalDateTime value
    ) {
        append(LOCAL_DATE_TIME_RANGE_STEP, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateTimeRangeWithPattern(
        @LocalDateTimeRangeSource(
            min = "01/01/2022 12:30",
            max = "01/01/2022 19:30",
            dateTimeFormat = "MM/dd/yyyy HH:mm"
        ) LocalDateTime value
    ) {
        append(LOCAL_DATE_TIME_RANGE_WITH_PATTERN, value);
    }

    @ParameterizedTest
    @ParametersSource
    void testLocalDateTimeRangeStepWithPattern(
        @LocalDateTimeRangeSource(
            min = "01/01/2022 12:30",
            max = "01/01/2022 19:30",
            increment = "PT30m",
            dateTimeFormat = "MM/dd/yyyy HH:mm"
        ) LocalDateTime value
    ) {
        append(LOCAL_DATE_TIME_RANGE_STEP_WITH_PATTERN, value);
    }
}
