package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ArgumentsBuilder {

    private final List<Object> arguments;

    static ArgumentsBuilder create(Object firstArgument) {
        return new ArgumentsBuilder(firstArgument);
    }

    public static ArgumentsBuilder create(ExtensionContext extensionContext) {
        Method requiredTestMethod = extensionContext.getRequiredTestMethod();
        Class<?>[] parameterTypes = requiredTestMethod.getParameterTypes();
        ArgumentsBuilder builder = new ArgumentsBuilder();
        Stream.of(parameterTypes)
            .forEach(builder::arg);
        return builder;
    }

    private ArgumentsBuilder() {
        arguments = new ArrayList<>();
    }

    private ArgumentsBuilder(Object firstArgument) {
        arguments = new ArrayList<>();
        arguments.add(firstArgument);
    }

    public ArgumentsBuilder arg(Object argument) {
        arguments.add(argument);
        return this;
    }

    public Stream<? extends Arguments> build() {

        return StreamSupport.stream(((Iterable<Arguments>) () -> new ParameterPermutationsIterator(arguments)).spliterator(), false);
    }
}
