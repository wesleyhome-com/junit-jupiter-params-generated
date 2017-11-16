package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.ParameterizedTest;

class MethodArgumentsProviderTest {


    @ParameterizedTest
    @ParametersSource
    void testParameters(Boolean bool) {

    }
}