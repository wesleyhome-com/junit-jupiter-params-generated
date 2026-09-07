package com.wesleyhome.test.jupiter.generator

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.size
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
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
    fun testSourceAnnotationOnUnsupportedTypeIsAnError() {
        val testModel = TestModel(
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = String::class,
                    annotations = listOf(IntRangeSource(min = 1, max = 10))
                )
            )
        )
        assertFailure { ParametersGenerator(testModel).arguments() }
            .hasMessage("Unable to find a suitable data provider for parameter [Parameter] with type 'kotlin.String'")
    }

    @Test
    fun testUnrecognizedAnnotationLeavesParameterToOtherResolvers() {
        val testModel = TestModel(
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = TestClass::class,
                    annotations = listOf(InvalidAnnotation())
                )
            )
        )
        assertThat(ParametersGenerator(testModel).parameterIndices).isEmpty()
    }

    @Test
    fun testUnannotatedUnsupportedTypeLeavesParameterToOtherResolvers() {
        val testModel = TestModel(
            testParameters = listOf(
                TestParameter(
                    name = "Parameter",
                    type = TestClass::class
                )
            )
        )
        assertThat(ParametersGenerator(testModel).parameterIndices).isEmpty()
    }

    @Test
    fun testOnlyGeneratedParametersAreClaimed() {
        val testModel = TestModel(
            testParameters = listOf(
                TestParameter(name = "notOurs", type = TestClass::class),
                TestParameter(name = "flag", type = Boolean::class),
                TestParameter(name = "alsoNotOurs", type = TestClass::class),
            )
        )
        val generator = ParametersGenerator(testModel)
        assertThat(generator.parameterIndices).isEqualTo(listOf(1))
        assertThat(generator.arguments().toList().map { it.get().toList() })
            .isEqualTo(listOf(listOf(true), listOf(false)))
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
