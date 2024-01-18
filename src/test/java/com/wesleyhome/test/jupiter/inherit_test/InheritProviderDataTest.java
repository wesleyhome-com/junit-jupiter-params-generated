package com.wesleyhome.test.jupiter.inherit_test;

import com.wesleyhome.test.jupiter.ParametersSource;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;

public abstract class InheritProviderDataTest {

    @ParameterizedTest
    @ParametersSource
    void findImplementedAbstractMethod(double doubles) {
        System.out.println(doubles);
    }

    abstract List<Double> data();
}
