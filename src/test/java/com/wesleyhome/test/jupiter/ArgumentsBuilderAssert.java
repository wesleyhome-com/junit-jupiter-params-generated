package com.wesleyhome.test.jupiter;

import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.ObjectAssertFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ArgumentsBuilderAssert extends
        AbstractListAssert<ArgumentsBuilderAssert, List<? extends Arguments>, Arguments, ObjectAssert<Arguments>> {
    //        AbstractAssert<ArgumentsBuilderAssert, ArgumentsBuilder> {
    private final ExtensionContext context;

    private ArgumentsBuilderAssert(ArgumentsBuilder argumentsBuilder, ExtensionContext context) throws Exception {
        super(argumentsBuilder.build().provideArguments(context).collect(Collectors.toList()), ArgumentsBuilderAssert.class);
        this.context = context;
    }

    public static ArgumentsBuilderAssert assertThat(ArgumentsBuilder builder) throws Exception {
        return assertThat(builder, null);
    }

    public static ArgumentsBuilderAssert assertThat(ArgumentsBuilder builder, ExtensionContext context) throws Exception {
        ExtensionContext _context = context != null ? context : new ExtensionContext() {
            @Override
            public Optional<ExtensionContext> getParent() {
                return null;
            }

            @Override
            public ExtensionContext getRoot() {
                return null;
            }

            @Override
            public String getUniqueId() {
                return null;
            }

            @Override
            public String getDisplayName() {
                return null;
            }

            @Override
            public Set<String> getTags() {
                return null;
            }

            @Override
            public Optional<AnnotatedElement> getElement() {
                return null;
            }

            @Override
            public Optional<Class<?>> getTestClass() {
                return null;
            }

            @Override
            public Optional<Object> getTestInstance() {
                return null;
            }

            @Override
            public Optional<Method> getTestMethod() {
                return null;
            }

            @Override
            public Optional<Throwable> getExecutionException() {
                return null;
            }

            @Override
            public void publishReportEntry(Map<String, String> map) {

            }

            @Override
            public Store getStore(Namespace namespace) {
                return null;
            }
        };
        return new ArgumentsBuilderAssert(builder, context);
    }

    public ArgumentsBuilderAssert isEqualTo(ArgumentsBuilder expected) {
        if (expected == null && actual == null) {
            return myself;
        }
        if (expected instanceof ArgumentsBuilder) {
            ArgumentsBuilder _expected = (ArgumentsBuilder) expected;
            try {
                return super.isEqualTo(_expected.build().provideArguments(context).collect(Collectors.toList()));
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
        return myself;
    }

    @Override
    protected ObjectAssert<Arguments> toAssert(Arguments value, String description) {
        return new ObjectAssertFactory<Arguments>().createAssert(value);
    }
}
