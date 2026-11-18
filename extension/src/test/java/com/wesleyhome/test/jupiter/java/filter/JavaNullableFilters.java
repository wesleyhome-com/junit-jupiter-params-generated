package com.wesleyhome.test.jupiter.java.filter;

/** A Java filter for a Kotlin test, which extends this class to find it. Driven by ParameterFilterTest. */
public class JavaNullableFilters {

    public static boolean javaNotB(String label) {
        return !"b".equals(label);
    }
}
