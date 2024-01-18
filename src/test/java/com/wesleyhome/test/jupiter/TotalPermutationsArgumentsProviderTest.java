package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.params.provider.Arguments.of;
//import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TotalPermutationsArgumentsProviderTest {

    //    private ArgumentsBuilder builder;
    private final Arguments trueArr = of(true);
    private final Arguments falseArr = of(false);
    private final Arguments nullArr = of(new Object[] {null});

    @Mock
    private ExtensionContext extensionContext;

    // @Test
    // void provideArguments_enum_class() {
    //   ParametersGenerator.Companion.create(extensionContext);
    //   ArgumentsBuilder builder = create(this, TestEnum.class, "provideArguments_enum_class");
    //   assertTestEnum(builder);
    // }
    //
    // private void assertTestEnum(ArgumentsBuilder builder) {
    //   int expectedSize = TestEnum.values().length;
    //   assertThat(builder).hasSize(expectedSize).containsExactlyInAnyOrder(
    //     of(TestEnum.GOOD),
    //     of(TestEnum.BAD),
    //     of(TestEnum.UGLY),
    //     of(TestEnum.GENIUS),
    //     of(TestEnum.INDIFFERENT),
    //     of(TestEnum.SMART),
    //     of(TestEnum.WEIRD)
    //   );
    // }
    //
    // @Test
    // void provideArguments_boolean_class() {
    //   ArgumentsBuilder builder = create(this, Boolean.class, "provideArguments_boolean_class");
    //
    //   assertThat(builder).hasSize(3).containsExactlyInAnyOrder(trueArr, falseArr, nullArr);
    // }
    //
    // @Test
    // void provideArguments_boolean_type() {
    //   ArgumentsBuilder builder = create(this, Boolean.TYPE, "provideArguments_boolean_type");
    //   assertThat(builder).hasSize(2).containsExactlyInAnyOrder(trueArr, falseArr);
    // }
}
