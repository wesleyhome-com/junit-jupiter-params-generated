package com.wesleyhome.test.jupiter.java.filter;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import org.junit.jupiter.api.Test;

/** Driven by ParameterFilterTest; not run directly. */
public class JavaFilterFixture {

    @GeneratedParametersTest(name = "{arguments}")
    public void price(@IntRangeSource(min = 1, max = 3) int start) {
    }

    @Test
    public void price_filter_applies() {
    }

    public static boolean price_filter_odd(int start) {
        return start % 2 == 1;
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void ordered(@IntRangeSource(min = 1, max = 3) int start) {
    }

    public static boolean ordered_filter_$small(int start) {
        return start <= 2;
    }
}
