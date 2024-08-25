package com.wesleyhome.test.jupiter.provider

import kotlin.reflect.KClass

class EnumParameterDataProviderTest : ParameterDataProviderTest<EnumParameterDataProvider, Enum<*>>() {

    override val parameterType: KClass<Enum<*>> by lazy { StateValue::class as KClass<Enum<*>> }

    override fun createFalseProvidesForTestParameter(): TestParameter {
        return createTestParameter(true).copy(type = Int::class)
    }
}

enum class StateValue {
    PENDING,
    STARTED,
    DONE
}
