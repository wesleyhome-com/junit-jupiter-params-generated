package com.wesleyhome.test.jupiter.provider

abstract class AbstractParameterDataProviderTest<P : AbstractParameterDataProvider<T>, T : Any> : ParameterDataProviderTest<P, T>() {
    override fun createFalseProvidesForTestParameter(): TestParameter {
        return createTestParameter(true).copy(type = InvalidType::class)
    }
}

class InvalidType
