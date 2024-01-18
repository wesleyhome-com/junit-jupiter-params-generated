package com.wesleyhome.test.jupiter;

import java.util.stream.Stream;

public class StreamHelper {

    @SafeVarargs
    public static <T> Stream<T> concat(Stream<T>... streams) {
        return Stream.of(streams).reduce(Stream::concat).orElseGet(Stream::empty);
    }

    public static <T> Stream<T> stream(T first, T[] others) {
        return concat(Stream.of(first), Stream.of(others));
    }
}
