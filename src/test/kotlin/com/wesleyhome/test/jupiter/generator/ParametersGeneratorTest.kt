package com.wesleyhome.test.jupiter.generator

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.size
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.Test

class ParametersGeneratorTest {

    @Test
    fun testBooleanArguments() {
        val testModel = TestModel(
            name = "name",
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = Boolean::class
                )
            )
        )

        val arguments = ParametersGenerator(testModel).arguments().toList()
        assertThat(arguments).size().isEqualTo(2)
    }


    @Test
    fun testBadArguments() {
        val testModel = TestModel(
            name = "name",
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = TestClass::class
                )
            )
        )
        assertFailure { ParametersGenerator(testModel).arguments() }
            .hasMessage("Unable to find a suitable data provider for parameter [Parameter] with type 'com.wesleyhome.test.jupiter.generator.TestClass'")
    }
}

class TestClass
