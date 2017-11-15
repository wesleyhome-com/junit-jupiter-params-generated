package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class TotalPermutationsArgumentsProviderTest {

    enum TestEnum {
        GOOD,
        BAD,
        UGLY,
        INDIFFERENT,
        WEIRD,
        SMART,
        GENIUS
    }

    private TotalPermutationsArgumentsProvider provider;
    private Object[] trueArr = a(true);
    private Object[] falseArr = a(false);
    private Object[] nullArr = new Object[]{null};

    @ParameterizedTest
    @EnumSource(TestEnum.class)
    void provideArguments_enum_const(TestEnum testEnum) throws Exception {
        provider = new TotalPermutationsArgumentsProvider(testEnum);
        assertTestEnum();
    }

    @Test
    void provideArguments_enum_class() throws Exception {
        provider = new TotalPermutationsArgumentsProvider(TestEnum.class);
        assertTestEnum();
    }

    private void assertTestEnum() throws Exception {
        int expectedSize = TestEnum.values().length;
        List<Object[]> collect = provider.provideArguments(null)
                .map(arg -> arg.get())
                .collect(toList());
        assertThat(collect).hasSize(expectedSize).containsExactlyInAnyOrder(a(TestEnum.GOOD),
                a(TestEnum.BAD),
                a(TestEnum.UGLY),
                a(TestEnum.GENIUS),
                a(TestEnum.INDIFFERENT),
                a(TestEnum.SMART),
                a(TestEnum.WEIRD)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void provideArguments_boolean(String value) throws Exception {
        provider = new TotalPermutationsArgumentsProvider(Boolean.valueOf(value));
        assertThat(
                provider.provideArguments(null)
                        .map(arg -> arg.get())
                        .collect(toList())
        ).hasSize(2).containsExactlyInAnyOrder(trueArr, falseArr);
    }

    @Test
    void provideArguments_boolean_class() throws Exception {
        provider = new TotalPermutationsArgumentsProvider(Boolean.class);

        assertThat(
                provider.provideArguments(null)
                        .map(arg -> arg.get())
                        .collect(toList())
        ).hasSize(3).containsExactlyInAnyOrder(trueArr, falseArr, nullArr);
    }

    @Test
    void provideArguments_boolean_type() throws Exception {
        provider = new TotalPermutationsArgumentsProvider(Boolean.TYPE);
        assertThat(
                provider.provideArguments(null)
                        .map(arg -> arg.get())
                        .collect(toList())
        ).hasSize(2).containsExactlyInAnyOrder(trueArr, falseArr);
    }

    Object[] a(Object... objects) {
        return objects;
    }

}
