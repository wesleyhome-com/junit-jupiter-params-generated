package com.wesleyhome.test.jupiter;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;
import org.assertj.core.util.Lists;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.util.Lists.newArrayList;

@SuppressWarnings("UnusedReturnValue")
public class ArgumentsBuilderAssert extends
    AbstractListAssert<ArgumentsBuilderAssert, List<? extends Arguments>, Arguments, ObjectAssert<Arguments>> {

    private ArgumentsBuilderAssert(ArgumentsBuilder argumentsBuilder) {
        this(argumentsBuilder.build().collect(Collectors.toList()));
    }

    private ArgumentsBuilderAssert(List<? extends Arguments> collect) {
        super(collect, ArgumentsBuilderAssert.class);
    }


    static ArgumentsBuilderAssert assertThat(ArgumentsBuilder builder) {
        return new ArgumentsBuilderAssert(builder);
    }


    public ArgumentsBuilderAssert isEqualTo(ArgumentsBuilder expected) {
        return expected == null ?
            super.isEqualTo(actual) :
            super.isEqualTo(expected.build().collect(Collectors.toList()));
    }

    @Override
    protected ObjectAssert<Arguments> toAssert(Arguments value, String description) {
        return new ObjectAssertFactory<Arguments>().createAssert(value);
    }

    @Override
    protected ArgumentsBuilderAssert newAbstractIterableAssert(Iterable<? extends Arguments> iterable) {
        return new ArgumentsBuilderAssert(newArrayList(iterable));
    }
}
