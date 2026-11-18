package com.wesleyhome.test.jupiter.consumer;

/** Methods whose names are only in the debug information, read directly by ParameterNamesTest. */
public class JavaConsumerFilters {

    public static boolean sharedOrdered(int start, int end) {
        return start <= end;
    }

    public static boolean overloaded(int start) {
        return start > 1;
    }

    public static boolean overloaded(long big, int small) {
        return small > big;
    }
}
