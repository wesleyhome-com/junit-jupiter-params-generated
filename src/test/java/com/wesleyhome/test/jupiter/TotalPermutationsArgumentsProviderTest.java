package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

import static com.wesleyhome.test.jupiter.ArgumentsBuilder.create;
import static com.wesleyhome.test.jupiter.ArgumentsBuilderAssert.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;
//import static org.assertj.core.api.Assertions.assertThat;

class TotalPermutationsArgumentsProviderTest {

    //    private ArgumentsBuilder builder;
    private final Arguments trueArr = of(true);
    private final Arguments falseArr = of(false);
    private final Arguments nullArr = of(new Object[]{null});

    @Test
    void provideArguments_enum_class() {
        ArgumentsBuilder builder = create(this, TestEnum.class);
        assertTestEnum(builder);
    }

    private void assertTestEnum(ArgumentsBuilder builder) {
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

    @Test
    void provideArguments_boolean_class() {
        ArgumentsBuilder builder = create(this, Boolean.class);

        assertThat(builder).hasSize(3).containsExactlyInAnyOrder(trueArr, falseArr, nullArr);
    }

    @Test
    void provideArguments_boolean_type() {
        ArgumentsBuilder builder = create(this, Boolean.TYPE);
        assertThat(builder).hasSize(2).containsExactlyInAnyOrder(trueArr, falseArr);
    }


}
