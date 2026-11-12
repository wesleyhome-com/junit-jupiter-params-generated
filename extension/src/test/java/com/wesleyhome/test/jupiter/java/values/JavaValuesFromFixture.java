package com.wesleyhome.test.jupiter.java.values;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.ValuesFrom;
import com.wesleyhome.test.jupiter.generator.Point;

import java.util.List;

/** Driven by ValuesFromTest; not run directly. */
public class JavaValuesFromFixture {

    @GeneratedParametersTest(name = "{arguments}")
    public void fromStatic(@ValuesFrom("points") Point point) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    public void fromRawList(@ValuesFrom("rawList") Point point) {
    }

    public static List<Point> points() {
        return List.of(new Point(40, 40));
    }

    @SuppressWarnings("rawtypes")
    public static List rawList() {
        return List.of("not a point");
    }
}
