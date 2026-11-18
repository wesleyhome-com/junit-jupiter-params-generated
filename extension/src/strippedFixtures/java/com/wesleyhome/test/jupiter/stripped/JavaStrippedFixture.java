package com.wesleyhome.test.jupiter.stripped;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.ValuesFrom;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import org.junit.jupiter.api.TestInfo;

/**
 * Compiled with -g:none and without -parameters, so no parameter name is recorded anywhere.
 * Driven by ParameterNamesTest; not run directly.
 */
public class JavaStrippedFixture extends KotlinNamedFilters {

    @GeneratedParametersTest(name = "{arguments}")
    public void ordered(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    static boolean ordered_filter(int start, int end) {
        return start <= end;
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void reversed(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    static boolean reversed_filter(int end, int start) {
        return start <= end;
    }

    @GeneratedParametersTest(name = "{arguments}", filters = {"sharedOrdered"})
    public void namedFilter(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void afterTestInfo(TestInfo testInfo, @IntRangeSource(min = 1, max = 3) int value) {
    }

    static boolean afterTestInfo_filter(int value) {
        return value != 2;
    }

    @GeneratedParametersTest
    public void displayed(@IntRangeSource(min = 1, max = 2) int start, @IntRangeSource(min = 1, max = 2) int end) {
    }

    @GeneratedParametersTest
    public void valuesFromMissing(@ValuesFrom("nope") String label) {
    }
}
