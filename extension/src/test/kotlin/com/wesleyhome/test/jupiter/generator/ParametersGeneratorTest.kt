package com.wesleyhome.test.jupiter.generator

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.size
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.TestParameter
import org.junit.jupiter.api.Test

class ParametersGeneratorTest {

    @Test
    fun testBooleanArguments() {
        val testModel = TestModel(
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
    fun testInvalidSourceProvider() {
        val testModel = TestModel(
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = TestClass::class,
                    annotations = listOf(InvalidSourceProviderAnnotation())
                )
            )
        )
        assertFailure { ParametersGenerator(testModel).arguments() }
            .hasMessage("Unable to find a suitable data provider for parameter [Parameter] with type 'com.wesleyhome.test.jupiter.generator.TestClass'")
    }

    @Test
    fun testInvalidAnnotation() {
        val testModel = TestModel(
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = TestClass::class,
                    annotations = listOf(InvalidAnnotation())
                )
            )
        )
        assertFailure { ParametersGenerator(testModel).arguments() }
            .hasMessage("Unable to find a suitable data provider for parameter [Parameter] with type 'com.wesleyhome.test.jupiter.generator.TestClass'")
    }

    @Test
    fun testBadArguments() {
        val testModel = TestModel(
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

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(TestClass::class)
annotation class InvalidSourceProviderAnnotation

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class InvalidAnnotation
