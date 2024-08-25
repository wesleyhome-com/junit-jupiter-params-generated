package com.wesleyhome.test.jupiter.java.provider;

import com.wesleyhome.test.jupiter.provider.ParameterDataProvider;
import com.wesleyhome.test.jupiter.provider.TestParameter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import kotlin.reflect.KClass;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

abstract class ParameterDataProviderTest<P extends ParameterDataProvider<T>, T extends Object> {

    private final List<Type> actualTypeArguments;
    protected final Class<P> providerType;
    protected final Class<T> parameterType;
    protected final P provider;

    ParameterDataProviderTest() {
        var genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.actualTypeArguments = Arrays.asList(genericSuperclass.getActualTypeArguments());
        this.providerType = (Class<P>) this.actualTypeArguments.get(0);
        this.parameterType = initializeParameterType();
        this.provider = createProvider();
    }

    protected Class<T> initializeParameterType() {
        return (Class<T>) actualTypeArguments.get(1);
    }

    protected P createProvider() {
        try {
            return providerType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected TestParameter createTestParameter() {
        return createTestParameter(true);
    }

    protected TestParameter createTestParameter(boolean isNullable) {
        return new TestParameter("testCreateParameterOptionsData", kotlin(parameterType), isNullable, List.of());
    }

    protected void testCreateParameterOptionsData(
        boolean isNullable,
        ListAssertBlock<T> listAssertBlock
    ) {
        var data = provider.createParameterOptionsData(createTestParameter(isNullable));
        listAssertBlock.execute(assertThat(data));
    }

    @Test
    void testTrueProvidesDataFor() {
        var testParameter = createTrueProvidesForTestParameter();
        assertThat(provider.providesDataFor(testParameter)).isTrue();
    }

    @Test
    void testFalseProvidesDataFor() {
        var testParameter = createFalseProvidesForTestParameter();
        assertThat(provider.providesDataFor(testParameter)).isFalse();
    }

    protected TestParameter createTrueProvidesForTestParameter() {
        return createTestParameter(true);
    }

    abstract TestParameter createFalseProvidesForTestParameter();

    protected void testCreateParameterOptionsDataWithExceptions(
        TestParameter testParameter,
        ThrowableAssertBlock throwableAssertBlock
    ) {
        AbstractThrowableAssert<? extends AbstractThrowableAssert<?, ?>, ?> thrownBy =
            Assertions.assertThatThrownBy(() -> provider.createParameterOptionsData(testParameter));
        throwableAssertBlock.execute(thrownBy);
    }

    protected void testCreateParameterOptionsDataWithExceptions(
        boolean isNullable,
        ThrowableAssertBlock throwableAssertBlock
    ) {
        AbstractThrowableAssert<? extends AbstractThrowableAssert<?, ?>, ?> thrownBy =
            Assertions.assertThatThrownBy(() -> provider.createParameterOptionsData(createTestParameter(isNullable)));
        throwableAssertBlock.execute(thrownBy);
    }

    protected void testCreateParameterOptionsData(
        TestParameter testParameter,
        ListAssertBlock<T> listAssertBlock
    ) {
        assertThat(testParameter).isNotNull();
        var data = provider.createParameterOptionsData(testParameter);
        listAssertBlock.execute(assertThat(data));
    }

    protected <K> KClass<K> kotlin(Class<K> cls) {
        return kotlin.jvm.JvmClassMappingKt.getKotlinClass(cls);
    }
}

@FunctionalInterface
interface ListAssertBlock<T> {

    void execute(ListAssert<T> list);
}

@FunctionalInterface
interface ThrowableAssertBlock {

    void execute(AbstractThrowableAssert<? extends AbstractThrowableAssert<?, ?>, ?> assertion);
}
