package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.invocationCounts
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.TestReporter
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

/**
 * Parameters this extension does not generate must be left to whichever other [ParameterResolver]
 * owns them, rather than failing the whole container.
 *
 * The fixture asserts that each injected value arrived intact; the counts here assert that every
 * combination ran and none of those assertions failed.
 */
class ParameterResolverCoexistenceTest {

    @Test
    fun testAlongsideBuiltInResolvers() {
        assertThat(invocationCounts(Fixture::class.java, "alongsideBuiltInResolvers"))
            .isEqualTo(3L to 3L)
    }

    @Test
    fun testAlongsideACustomResolver() {
        assertThat(invocationCounts(Fixture::class.java, "alongsideACustomResolver"))
            .isEqualTo(2L to 2L)
    }

    class Fixture {

        @GeneratedParametersTest
        fun alongsideBuiltInResolvers(
            @IntRangeSource(min = 1, max = 3) value: Int,
            testInfo: TestInfo,
            testReporter: TestReporter,
            @TempDir tempDir: Path
        ) {
            assertThat(value).isEqualTo(value.coerceIn(1, 3))
            assertThat(testInfo.displayName).isNotNull()
            assertThat(testReporter).isNotNull()
            assertThat(tempDir.toFile().isDirectory).isTrue()
        }

        /** The resolved parameter comes first, so the generated one is not at index 0. */
        @GeneratedParametersTest
        fun alongsideACustomResolver(
            greeting: String,
            @IntRangeSource(min = 1, max = 2) value: Int
        ) {
            assertThat(greeting).isEqualTo(GREETING)
            assertThat(value).isEqualTo(value.coerceIn(1, 2))
        }

        companion object {
            private const val GREETING = "resolved elsewhere"

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
