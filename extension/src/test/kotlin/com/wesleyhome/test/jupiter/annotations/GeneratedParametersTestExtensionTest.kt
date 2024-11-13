package com.wesleyhome.test.jupiter.annotations

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import com.wesleyhome.test.jupiter.generator.GeneratedParametersTestExtension
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaMethod

@ExtendWith(MockKExtension::class)
class GeneratedParametersTestExtensionTest {

    private val ext = GeneratedParametersTestExtension()

    @MockK
    private lateinit var context: ExtensionContext

    @RelaxedMockK
    private lateinit var store: ExtensionContext.Store

    @GeneratedParametersTest
    fun testSupportsTestTemplate(isPresent: Boolean, isAnnotated: Boolean, isParameterizedTest: Boolean) {
        every { context.testMethod.isPresent } returns isPresent
        if (isPresent) {
            val testFunction = this::class.declaredFunctions
                .filter { it.hasAnnotation<Disabled>() }
                .filter { it.hasAnnotation<GeneratedParametersTest>() == isAnnotated }
                .firstOrNull { it.hasAnnotation<ParameterizedTest>() == isParameterizedTest }
            val method = testFunction?.javaMethod!!
            every { context.requiredTestMethod } returns method
            if (isAnnotated && !isParameterizedTest) {
                every { context.getStore(any()) } returns store
            }
        }
        if (isPresent && isAnnotated && isParameterizedTest) {
            assertFailure { ext.supportsTestTemplate(context) }
                .hasMessage("Test annotated with @GeneratedParametersTest cannot be annotated with @ParameterizedTest")
            verify(exactly = 0) { store.put(any(), any()) }
        } else {
            val expected = isPresent && isAnnotated
            assertThat(ext.supportsTestTemplate(context)).isEqualTo(expected)
            if (expected) {
                verify { store.put("context", any()) }
            } else {
                verify { store wasNot Called }
            }
        }
    }

    @GeneratedParametersTest
    @ParameterizedTest
    @Disabled("Function is being used for testing above and is not meant to be a test itself")
    fun isAnnotatedAndIsParameterizedTest() {
    }

    @ParameterizedTest
    @Disabled("Function is being used for testing above and is not meant to be a test itself")
    fun isParameterizedTest() {
    }

    @GeneratedParametersTest
    @Disabled("Function is being used for testing above and is not meant to be a test itself")
    fun isAnnotated() {
    }

    @Disabled("Function is being used for testing above and is not meant to be a test itself")
    fun isNotAnnotated() {
    }
}
