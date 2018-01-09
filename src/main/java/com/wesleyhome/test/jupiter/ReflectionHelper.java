package com.wesleyhome.test.jupiter;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class ReflectionHelper {


    public static Stream<Constructor> getConstructorStream(Class<?> providerClass) {
        return getAOStream(providerClass, Class::getDeclaredConstructors, Class::getConstructors);
    }


    public static Stream<Method> getMethodStream(Class<?> providerClass) {
        return getAOStream(providerClass, Class::getDeclaredMethods, Class::getMethods);
    }

    public static Stream<Field> getFieldStream(Class<?> providerClass) {
        return getAOStream(providerClass, Class::getDeclaredFields, Class::getFields);
    }

    public static <T extends AccessibleObject> Stream<T> getAOStream(Class<?> cls, Function<Class<?>, T[]> function1, Function<Class<?>, T[]>... otherFunctions) {
        return Stream.concat(Stream.of(function1), Stream.of(otherFunctions))
            .map(f -> f.apply(cls))
            .flatMap(Stream::of);
    }

    public static <T> Stream<T> concat(Stream<T> stream1, Stream<T> stream2, Stream<T>... otherStreams) {
        Stream<T> concat = Stream.concat(stream1, stream2);
        if (otherStreams.length == 0) {
            return concat;
        }
        Stream<T>[] next = Arrays.copyOfRange(otherStreams, 1, otherStreams.length);
        return concat(concat, otherStreams[0], next);
    }

    public static <R> R invoke(Method method, Object target, Object... parameters) {
        return _invoke(method, m -> {
            try {
                return (R) method.invoke(target, parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static <T extends AccessibleObject, R> R _invoke(T accessibleObject, Function<T, R> function) {
        boolean accessible = accessibleObject.isAccessible();
        if (!accessible) {
            accessibleObject.setAccessible(true);
        }
        try {
            return function.apply(accessibleObject);
        } finally {
            if (!accessible) {
                accessibleObject.setAccessible(accessible);
            }
        }
    }

    public static <T> T invokeConstructor(Class<T> cls, Object... parameter) {
        try {
            Class<?>[] parameterTypes = Stream.of(parameter)
                .map(Object::getClass)
                .toArray(Class<?>[]::new);
            cls.getConstructors();
            Constructor<T> constructor = cls.getConstructor(parameterTypes);
            return _invoke(constructor, c -> {
                try {
                    return c.newInstance(parameter);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
