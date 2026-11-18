package com.wesleyhome.test.jupiter.consumer;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.StringSource;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;

/** Overloaded filters compiled without -parameters. Driven by ParameterNamesTest; not run directly. */
public class JavaOverloadsFixture {

    @GeneratedParametersTest(name = "{arguments}", filters = {"rule"})
    public void mixed(@IntRangeSource(min = 1, max = 3) int start, @StringSource(values = {"a", "bb"}) String label) {
    }

    static boolean rule(int start) {
        return start >= 2;
    }

    static boolean rule(String label) {
        return label.length() > 1;
    }
}
