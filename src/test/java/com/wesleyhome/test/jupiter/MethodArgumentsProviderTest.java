package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.ParameterizedTest;

class MethodArgumentsProviderTest {


    @ParameterizedTest
    @ParametersSource
    void testParameters(Boolean bool) {
        System.out.println(bool);
    }

    @ParameterizedTest
    @ParametersSource
    void testParameters_primitive(boolean bool) {
        System.out.println(bool);
    }

    @ParameterizedTest
    @ParametersSource
    void testParameters_enum(TestEnum testEnum) {
        System.out.println(testEnum);
    }

    @ParameterizedTest
    @ParametersSource
    void testParameters_lots(TestEnum testEnum, Boolean wrap, boolean bool) {
        System.out.printf("%s - %s - %s%n", testEnum, wrap, bool);
    }
}