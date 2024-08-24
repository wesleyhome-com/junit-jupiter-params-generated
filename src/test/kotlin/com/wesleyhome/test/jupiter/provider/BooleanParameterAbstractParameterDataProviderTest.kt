package com.wesleyhome.test.jupiter.provider

import org.junit.jupiter.api.Test

class BooleanParameterAbstractParameterDataProviderTest : AbstractParameterDataProviderTest<BooleanParameterDataProvider, Boolean>() {

    @Test
    fun testCreateBooleanOptionsData() {
        testCreateParameterOptionsData(isNullable = false) {
            it.containsExactlyInAnyOrder(true, false)
        }
    }
}
