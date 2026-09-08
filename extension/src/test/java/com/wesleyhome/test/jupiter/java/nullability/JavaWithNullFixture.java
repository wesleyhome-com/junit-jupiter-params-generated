package com.wesleyhome.test.jupiter.java.nullability;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.WithNull;
import com.wesleyhome.test.jupiter.annotations.number.IntSource;

/** Driven by WithNullTest; not run directly. */
public class JavaWithNullFixture {

    @GeneratedParametersTest(name = "{arguments}")
    public void boxedWithAnnotation(@IntSource(values = {1, 2}) @WithNull Integer value) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void boxedWithoutAnnotation(@IntSource(values = {1, 2}) Integer value) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void primitiveWithAnnotation(@IntSource(values = {1, 2}) @WithNull int value) {
    }
}
