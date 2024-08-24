package com.wesleyhome.test.jupiter.provider

import javax.swing.SwingWorker.StateValue
import kotlin.reflect.KClass

class EnumParameterDataProviderTest : ParameterDataProviderTest<EnumParameterDataProvider, Enum<*>>() {

    override val parameterType: KClass<Enum<*>> by lazy { StateValue::class as KClass<Enum<*>> }


    override fun createFalseProvidesForTestParameter(): TestParameter {
        return createTestParameter(true).copy(type = Int::class)
    }
}
