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
    private final List<Class<?>> dataProviderClasses;

    static ArgumentsBuilder create(Class<?> firstArgument) {
        return new ArgumentsBuilder(firstArgument, new ArrayList<>());
    }

    public static ArgumentsBuilder create(ExtensionContext extensionContext) {
        Method requiredTestMethod = extensionContext.getRequiredTestMethod();
        Class<?> requiredTestClass = extensionContext.getRequiredTestClass();
        Class<?>[] dataProviderClasses = requiredTestMethod.getAnnotation(ParametersSource.class).value();
        Class<?>[] parameterTypes = requiredTestMethod.getParameterTypes();
        List<Class<?>> dataProviderList = Stream.concat(Stream.of(requiredTestClass), Stream.of(dataProviderClasses))
            .collect(Collectors.toList());
        ArgumentsBuilder builder = new ArgumentsBuilder(dataProviderList);
        Stream.of(parameterTypes)
            .forEach(builder::arg);
        return builder;
    }

    private ArgumentsBuilder(final List<Class<?>> dataProviderClasses) {
        this.dataProviderClasses = dataProviderClasses;
        this.arguments = new ArrayList<>();
    }

    private ArgumentsBuilder(Class<?> firstArgument, final List<Class<?>> dataProviderClasses) {
        this.dataProviderClasses = dataProviderClasses;
        this.arguments = new ArrayList<>();
        this.arguments.add(firstArgument);
    }

    private ArgumentsBuilder arg(Class<?> argument) {
        this.arguments.add(argument);
        return this;
    }

    public Stream<? extends Arguments> build() {
        return StreamSupport.stream(((Iterable<Arguments>) () -> new ParameterPermutationsIterator(arguments, dataProviderClasses)).spliterator(), false);
    }
}
