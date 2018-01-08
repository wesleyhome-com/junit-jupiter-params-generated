package com.wesleyhome.test.jupiter;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class ArgumentsBuilderAssert extends
    AbstractListAssert<ArgumentsBuilderAssert, List<? extends Arguments>, Arguments, ObjectAssert<Arguments>> {

    private ArgumentsBuilderAssert(ArgumentsBuilder argumentsBuilder) {
        super(argumentsBuilder.build().collect(Collectors.toList()), ArgumentsBuilderAssert.class);
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
}
