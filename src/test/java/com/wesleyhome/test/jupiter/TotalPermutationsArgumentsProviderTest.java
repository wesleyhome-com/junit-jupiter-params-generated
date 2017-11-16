package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.wesleyhome.test.jupiter.ArgumentsBuilder.create;
import static com.wesleyhome.test.jupiter.ArgumentsBuilderAssert.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;
//import static org.assertj.core.api.Assertions.assertThat;

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

    //    private ArgumentsBuilder builder;
    private Arguments trueArr = of(true);
    private Arguments falseArr = of(false);
    private Arguments nullArr = of(new Object[]{null});

    @Test
    void provideArguments_multiple_arguments() throws Exception {
        ArgumentsBuilder builder1 = create(true).arg(true).arg(TestEnum.values());
        ArgumentsBuilder builder2 = create(true).arg(true).arg(TestEnum.class);
        assertThat(builder1).isEqualTo(builder2);
    }

    @Test
    void provideArguments_object_arr() throws Exception {
        ArgumentsBuilder builder = create(TestEnum.values());
        assertTestEnum(builder);
    }

    @ParameterizedTest
    @EnumSource(TestEnum.class)
    void provideArguments_enum_const(TestEnum testEnum) throws Exception {
        ArgumentsBuilder builder = create(testEnum);
        assertTestEnum(builder);
    }

    @Test
    void provideArguments_enum_class() throws Exception {
        ArgumentsBuilder builder = create(TestEnum.class);
        assertTestEnum(builder);
    }

    private void assertTestEnum(ArgumentsBuilder builder) throws Exception {
        int expectedSize = TestEnum.values().length;
        assertThat(builder).hasSize(expectedSize).containsExactlyInAnyOrder(
                of(TestEnum.GOOD),
                of(TestEnum.BAD),
                of(TestEnum.UGLY),
                of(TestEnum.GENIUS),
                of(TestEnum.INDIFFERENT),
                of(TestEnum.SMART),
                of(TestEnum.WEIRD)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void provideArguments_boolean(String value) throws Exception {
        ArgumentsBuilder builder = create(Boolean.valueOf(value));
        assertThat(builder).hasSize(2).containsExactlyInAnyOrder(trueArr, falseArr);
    }

    @Test
    void provideArguments_boolean_class() throws Exception {
        ArgumentsBuilder builder = create(Boolean.class);

        assertThat(builder).hasSize(3).containsExactlyInAnyOrder(trueArr, falseArr, nullArr);
    }

    @Test
    void provideArguments_boolean_type() throws Exception {
        ArgumentsBuilder builder = create(Boolean.TYPE);
        assertThat(builder).hasSize(2).containsExactlyInAnyOrder(trueArr, falseArr);
    }


}
