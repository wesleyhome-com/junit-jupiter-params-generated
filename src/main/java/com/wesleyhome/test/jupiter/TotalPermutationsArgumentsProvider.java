package com.wesleyhome.test.jupiter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class TotalPermutationsArgumentsProvider implements ArgumentsProvider {


    private final Object[] parameterOptions;

    public TotalPermutationsArgumentsProvider(Object... parameterOptions){
        this.parameterOptions = parameterOptions;
    }

    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return StreamSupport.stream(new ParameterPermutationsIterable(parameterOptions).spliterator(), false);
    }
}
