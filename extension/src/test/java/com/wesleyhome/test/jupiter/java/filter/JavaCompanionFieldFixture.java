package com.wesleyhome.test.jupiter.java.filter;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;

/** Filters on an instance in a static field named like a Kotlin companion. Driven by ParameterFilterTest; not run directly. */
public class JavaCompanionFieldFixture {

    public static final Rules Companion = new Rules();

    @GeneratedParametersTest(name = "{arguments}")
    public void ordered(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    @GeneratedParametersTest(name = "{arguments}", filters = {"explicitRule"})
    public void explicit(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    public static class Rules {

        public boolean ordered_filter(int start, int end) {
            return start <= end;
        }

        public boolean explicitRule(int start, int end) {
            return start <= end;
        }
    }
}
