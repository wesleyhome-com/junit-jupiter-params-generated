package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class ArgumentsBuilder {

    private final List<Object> arguments;

    public static ArgumentsBuilder create(Object firstArgument) {
        return new ArgumentsBuilder(firstArgument);
    }

    private ArgumentsBuilder(Object firstArgument) {
        arguments = new ArrayList<>();
        arguments.add(firstArgument);
    }

    public ArgumentsBuilder arg(Object argument) {
        arguments.add(argument);
        return this;
    }

    public ArgumentsProvider build() {

        return extensionContext -> {
            Method requiredTestMethod = extensionContext.getRequiredTestMethod();
            return StreamSupport.stream(((Iterable<Arguments>) () -> new ParameterPermutationsIterator(arguments)).spliterator(), false);
        };
    }
}
