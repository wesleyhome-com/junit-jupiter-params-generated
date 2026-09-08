package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.RegisterExtension

/**
 * A resolver is asked about constructor and lifecycle-method parameters as well as test method
 * ones. Both the constructor parameter and the generated parameter below sit at index 0, so
 * matching on index alone hands the constructor an `Int` where it wants a `String`.
 */
class ConstructorParameterTest {

    @Test
    fun testConstructorParameterIsLeftToItsOwnResolver() {
        assertThat(invocationNames(Fixture::class.java, "generated")).isEqualTo(listOf("1", "2"))
    }

    @Test
    fun testBeforeEachParameterIsLeftToItsOwnResolver() {
        assertThat(invocationNames(Fixture::class.java, "generatedWithBeforeEach"))
            .isEqualTo(listOf("1", "2"))
    }

    class Fixture(private val greeting: String) {

        @GeneratedParametersTest(name = "{arguments}")
        fun generated(@IntRangeSource(min = 1, max = 2) value: Int) {
            assertThat(greeting).isEqualTo(GREETING)
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun generatedWithBeforeEach(@IntRangeSource(min = 1, max = 2) value: Int) {
            assertThat(greeting).isEqualTo(GREETING)
        }

        companion object {
            const val GREETING = "resolved elsewhere"

            @JvmStatic
            @RegisterExtension
            val greetingResolver: ParameterResolver = object : ParameterResolver {
                override fun supportsParameter(
                    parameterContext: ParameterContext,
                    extensionContext: ExtensionContext
                ): Boolean = parameterContext.parameter.type == String::class.java

                override fun resolveParameter(
                    parameterContext: ParameterContext,
                    extensionContext: ExtensionContext
                ): Any = GREETING
            }
        }
    }
}
