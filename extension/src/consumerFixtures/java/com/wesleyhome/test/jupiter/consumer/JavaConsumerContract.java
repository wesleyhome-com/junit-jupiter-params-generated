package com.wesleyhome.test.jupiter.consumer;

/** A filter on an interface the test class implements, whose own class file holds its names. */
public interface JavaConsumerContract {

    static boolean interfaceRule(int end, int start) {
        return start <= end;
    }
}
