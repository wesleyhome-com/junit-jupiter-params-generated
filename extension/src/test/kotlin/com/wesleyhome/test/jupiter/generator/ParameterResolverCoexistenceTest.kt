package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.api.TestReporter
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger

/**
 * Parameters this extension does not generate must be left to whichever other
 * [ParameterResolver] owns them, rather than failing the whole container.
 */
class ParameterResolverCoexistenceTest {

    @GeneratedParametersTest
    fun testAlongsideBuiltInResolvers(
        @IntRangeSource(min = 1, max = 3) value: Int,
        testInfo: TestInfo,
        testReporter: TestReporter,
        @TempDir tempDir: Path
    ) {
        assertThat(value).isBetween(1, 3)
        assertThat(testInfo.displayName).isNotNull()
        assertThat(testReporter).isNotNull()
        assertThat(tempDir.toFile().isDirectory).isTrue()
        builtInInvocations.incrementAndGet()
    }

    @GeneratedParametersTest
    fun testAlongsideCustomResolver(
        greeting: String,
        @IntRangeSource(min = 1, max = 2) value: Int
    ) {
        assertThat(greeting).isEqualTo(GREETING)
        assertThat(value).isBetween(1, 2)
        customInvocations.incrementAndGet()
    }

    companion object {
        private const val GREETING = "resolved elsewhere"
        private val builtInInvocations = AtomicInteger(0)
        private val customInvocations = AtomicInteger(0)

        @JvmStatic
        @RegisterExtension
        val greetingResolver: ParameterResolver = object : ParameterResolver {
            override fun supportsParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Boolean = parameterContext.parameter.name == "greeting" ||
                parameterContext.parameter.type == String::class.java

            override fun resolveParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Any = GREETING
        }

        @JvmStatic
        @AfterAll
        fun assertAllInvocationsRan() {
            assertThat(builtInInvocations.get()).isEqualTo(3)
            assertThat(customInvocations.get()).isEqualTo(2)
        }
    }
}
