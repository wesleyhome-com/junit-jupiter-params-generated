package com.wesleyhome.test.jupiter.java.display;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import org.junit.jupiter.api.TestInfo;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Java test methods reach the extension too, and with -parameters on the compile the generated
 * display name carries the declared parameter name rather than arg0.
 */
public class JavaDisplayNameTest {

    @GeneratedParametersTest
    void javaParameterNamesAppearInDisplayNames(
        @IntRangeSource(min = 1, max = 2) int value,
        TestInfo testInfo
    ) {
        assertThat(testInfo.getDisplayName()).isEqualTo("[" + value + "] value=" + value);
    }

    @GeneratedParametersTest(name = "java run {index} with {0}")
    void javaHonoursACustomNamePattern(
        @IntRangeSource(min = 1, max = 2) int value,
        TestInfo testInfo
    ) {
        assertThat(testInfo.getDisplayName()).isEqualTo("java run " + value + " with " + value);
    }
}
