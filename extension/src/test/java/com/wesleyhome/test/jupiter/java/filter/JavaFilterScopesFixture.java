package com.wesleyhome.test.jupiter.java.filter;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;
import org.junit.jupiter.api.Nested;

/** Filters declared somewhere other than the class that declares the test. Driven by ParameterFilterTest; not run directly. */
public class JavaFilterScopesFixture {

    static boolean ordered_filter(int start, int end) {
        return start <= end;
    }

    static boolean outerOrdered(int start, int end) {
        return start <= end;
    }

    @Nested
    public class Inner {

        @GeneratedParametersTest(name = "{arguments}")
        public void ordered(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = {"outerOrdered"})
        public void explicit(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
        }
    }

    public abstract static class Base {

        @GeneratedParametersTest(name = "{arguments}")
        public void inherited(@IntRangeSource(min = 1, max = 3) int start) {
        }
    }

    public static class Inheriting extends Base {

        static boolean inherited_filter(int start) {
            return start != 1;
        }
    }

    public interface Contract {

        @GeneratedParametersTest(name = "{arguments}")
        default void fromInterface(@IntRangeSource(min = 1, max = 3) int start) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = {"keepBig"})
        default void explicitFromInterface(@IntRangeSource(min = 1, max = 3) int start) {
        }

        static boolean fromInterface_filter(int start) {
            return start >= 2;
        }

        static boolean keepBig(int start) {
            return start >= 2;
        }
    }

    public static class Implementing implements Contract {
    }
}
