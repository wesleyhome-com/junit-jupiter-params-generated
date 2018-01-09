package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ArgumentsBuilder {

    private final List<Class<?>> arguments;
    private final Object testInstance;
    private final List<Class<?>> dataProviderClasses;

    static ArgumentsBuilder create(Object testInstance, Class<?> firstArgument) {
        return new ArgumentsBuilder(testInstance, firstArgument, new ArrayList<>());
    }

    static Stream<? extends Arguments> create(ExtensionContext extensionContext) {
        Method requiredTestMethod = extensionContext.getRequiredTestMethod();
        Class<?> requiredTestClass = extensionContext.getRequiredTestClass();
        Object testInstance = ReflectionHelper.invokeConstructor(requiredTestClass);
        Class<?>[] dataProviderClasses = requiredTestMethod.getAnnotation(ParametersSource.class).value();
        Class<?>[] parameterTypes = requiredTestMethod.getParameterTypes();
        List<Class<?>> dataProviderList = Stream.concat(Stream.of(requiredTestClass), Stream.of(dataProviderClasses))
            .collect(Collectors.toList());
        ArgumentsBuilder builder = new ArgumentsBuilder(testInstance, dataProviderList);
        Stream.of(parameterTypes)
            .forEach(builder::arg);
        return builder.build();
    }

    private ArgumentsBuilder(Object testInstance, final List<Class<?>> dataProviderClasses) {
        this.testInstance = testInstance;
        this.dataProviderClasses = dataProviderClasses;
        this.arguments = new ArrayList<>();
    }

    private ArgumentsBuilder(Object testInstance, Class<?> firstArgument, final List<Class<?>> dataProviderClasses) {
        this.testInstance = testInstance;
        this.dataProviderClasses = dataProviderClasses;
        this.arguments = new ArrayList<>();
        this.arguments.add(firstArgument);
    }

    private ArgumentsBuilder arg(Class<?> argument) {
        this.arguments.add(argument);
        return this;
    }

    public Stream<? extends Arguments> build() {
        return StreamSupport.stream(((Iterable<Arguments>) () -> new ParameterPermutationsIterator(arguments, testInstance, dataProviderClasses)).spliterator(), false);
    }
}
