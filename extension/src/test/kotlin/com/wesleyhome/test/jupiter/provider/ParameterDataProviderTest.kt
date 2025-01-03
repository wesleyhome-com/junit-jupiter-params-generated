package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.typeArguments
import com.wesleyhome.test.jupiter.kotlinType
import org.assertj.core.api.AbstractThrowableAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ListAssert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.reflect.KClass

abstract class ParameterDataProviderTest<P : ParameterDataProvider<T>, T : Any> {


    private val providerType: KClass<P> by lazy { typeArguments[0].kotlinType() }
    protected open val parameterType: KClass<T> by lazy { typeArguments[1].kotlinType() }
    protected lateinit var provider: P

    @BeforeEach
    fun setup() {
        provider = createProvider()
    }

    protected fun createTestParameter(isNullable: Boolean): TestParameter {
        return TestParameter(
            name = "testCreateParameterOptionsData",
            type = parameterType,
            isNullable = isNullable
        )
    }

    protected fun testCreateParameterOptionsData(
        testParameter: TestParameter? = null,
        isNullable: Boolean,
        block: (listAssert: ListAssert<T?>) -> Unit
    ) {
        val data = assertDoesNotThrow {
            val parameter = getOrDefault(testParameter) { createTestParameter(isNullable) }
            provider.createParameterOptionsData(parameter)
        }
        block(assertThat(data))
    }

    private fun <T> getOrDefault(value: T?, block: () -> T): T = value ?: block()

    protected fun testCreateParameterOptionsDataWithException(
        testParameter: TestParameter? = null,
        block: (throwable: AbstractThrowableAssert<*, out Throwable>) -> Unit
    ) {
        val assertThatThrownBy: AbstractThrowableAssert<*, out Throwable> = Assertions.assertThatThrownBy {
            val parameter = getOrDefault(testParameter) {
                createTestParameter(false)
            }
            provider.createParameterOptionsData(parameter)
        }
        block(assertThatThrownBy)
    }

    @Test
    fun testTrueProvidesDataFor() {
        val testParameter = createTrueProvidesForTestParameter()
        assertThat(provider.providesDataFor(testParameter)).isTrue()
    }

    @Test
    fun testFalseProvidesDataFor() {
        val testParameter = createFalseProvidesForTestParameter()
        assertThat(provider.providesDataFor(testParameter)).isFalse()
    }

    protected open fun createTrueProvidesForTestParameter(): TestParameter = createTestParameter(true)

    abstract fun createFalseProvidesForTestParameter(): TestParameter

    protected fun createProvider(): P = providerType.constructors.first().call()
}
