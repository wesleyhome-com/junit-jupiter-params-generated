package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.provider.Arguments;

import java.util.Iterator;

class ParameterPermutationsIterable implements Iterable<Arguments> {

    private final ParameterPermutationsIterator iterator;

    ParameterPermutationsIterable(Object... parameterOptions){
        iterator = new ParameterPermutationsIterator(parameterOptions);
    }

    @Override
    public Iterator<Arguments> iterator() {
        return iterator;
    }
}
