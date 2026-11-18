package com.wesleyhome.test.jupiter.consumer;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.ValuesFrom;
import com.wesleyhome.test.jupiter.annotations.number.DoubleSource;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import com.wesleyhome.test.jupiter.annotations.number.LongSource;
import com.wesleyhome.test.jupiter.stripped.JavaStrippedFilters;
import org.junit.jupiter.api.TestInfo;

/**
 * Compiled with Gradle's defaults and without -parameters, so parameter names are only in the
 * debug information. Driven by ParameterNamesTest; not run directly.
 */
public class JavaConsumerFixture extends JavaStrippedFilters implements JavaConsumerContract {

    private static final int ONE = 1;

    public static final Rules Companion = new Rules();

    @GeneratedParametersTest(name = "{arguments}")
    public void endNotOne(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    static boolean endNotOne_filter(int end) {
        return end != ONE;
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void ordered(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    static boolean ordered_filter(int end, int start) {
        return start <= end;
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void inOrder(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    static boolean inOrder_filter(int start, int end) {
        return start <= end;
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void afterTestInfo(TestInfo testInfo, @IntRangeSource(min = 1, max = 3) int value) {
    }

    /** The try block gives this class an exception table, which reading the class file has to skip. */
    static boolean afterTestInfo_filter(int value) {
        try {
            return value != Integer.parseInt("2");
        } catch (NumberFormatException e) {
            return true;
        }
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void wide(
        @LongSource(values = {1, 2}) long big,
        @DoubleSource(values = {0.5}) double ratio,
        @IntRangeSource(min = 1, max = 3) int small
    ) {
    }

    static boolean wide_filter(double ratio, long big, int small) {
        return small > big && ratio < 1;
    }

    @GeneratedParametersTest(name = "{arguments}", filters = {"interfaceRule"})
    public void fromInterface(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    @GeneratedParametersTest(name = "{arguments}", filters = {"strippedOrdered"})
    public void strippedFilter(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    @GeneratedParametersTest(name = "{arguments}", filters = {"companionRule"})
    public void companionField(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    @GeneratedParametersTest
    public void displayed(@IntRangeSource(min = 1, max = 2) int start, @IntRangeSource(min = 1, max = 2) int end) {
    }

    @GeneratedParametersTest(filters = {"mentionsMissing"})
    public void unknownParameter(@IntRangeSource(min = 1, max = 2) int start) {
    }

    static boolean mentionsMissing(int nope) {
        return true;
    }

    @GeneratedParametersTest(filters = {"expectsString"})
    public void wrongType(@IntRangeSource(min = 1, max = 2) int start) {
    }

    static boolean expectsString(String start) {
        return true;
    }

    @GeneratedParametersTest
    public void valuesFromMissing(@ValuesFrom("nope") String label) {
    }

    public static class Rules {

        public boolean companionRule(int end, int start) {
            return start <= end;
        }
    }
}
