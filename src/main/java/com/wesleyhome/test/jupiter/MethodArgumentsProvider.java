package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MethodArgumentsProvider implements ArgumentsProvider {


    private static <E extends Enum<E>> EnumSet<E> allOf(Class<?> enumClass) {
        return EnumSet.allOf((Class<E>) enumClass);
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        Method requiredTestMethod = context.getRequiredTestMethod();
        context.getRequiredTestClass();
        Class<?>[] parameterTypes = requiredTestMethod.getParameterTypes();
        List<Object[]> options = new ArrayList<>();

        for (Class<?> parameterType : parameterTypes) {
            if (parameterType.isEnum()) {
                EnumSet<?> enumSet = allOf(parameterType);
                options.add(enumSet.toArray());
            } else if (Boolean.TYPE.equals(parameterType)) {
                options.add(new Object[]{true, false});
            } else if (Boolean.class.equals(parameterType)) {
                options.add(new Object[]{true, false, null});
            } else {
                // can't figure out the class information
                options.add(new Object[]{null});
            }
        }
        Object[][] parameterOptions = new Object[0][0];

        return StreamSupport.stream(((Iterable<Arguments>) () -> new ParameterPermutationsIterator(parameterOptions)).spliterator(), true);
//        requiredTestMethod.getAnnotation(Parameter)
    }
}
