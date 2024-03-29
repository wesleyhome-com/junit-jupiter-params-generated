package com.wesleyhome.test.jupiter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unchecked")
public class ReflectionHelper {

    public static Stream<Constructor<?>> getConstructorStream(Class<?> providerClass) {
        return getAOStream(providerClass, Class::getDeclaredConstructors, Class::getConstructors);
    }

    private static <T extends AccessibleObject> Stream<T> getAOStream(
        Class<?> cls, Function<Class<?>, T[]> function1,
        Function<Class<?>, T[]>... otherFunctions
    ) {
        return StreamHelper.stream(function1, otherFunctions)
            .map(f -> f.apply(cls))
            .map(Stream::of)
            .reduce(Stream::concat)
            .orElseGet(Stream::empty);
    }

    private static <T extends AccessibleObject, R> R _invoke(T accessibleObject, Function<T, R> function) {
        boolean accessible = accessibleObject.isAccessible();
        if (!accessible) {
            accessibleObject.setAccessible(true);
        }
        try {
            return function.apply(accessibleObject);
        } finally {
            accessibleObject.setAccessible(accessible);
        }
    }

    public static <T> T invokeConstructor(Class<T> cls, Object... parameter) {
        try {
            Class<?>[] parameterTypes = Stream.of(parameter)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
            Constructor<T> constructor = (Constructor<T>) getConstructorStream(cls)
                .filter(c -> Arrays.equals(parameterTypes, c.getParameterTypes()))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("Unable to find matching constructor"));
            return _invoke(constructor, c1 -> {
                try {
                    return c1.newInstance(parameter);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
