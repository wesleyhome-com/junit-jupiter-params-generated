package com.wesleyhome.test.jupiter.annotations

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

class GeneratedParametersTestInvocationContext(
    private val arguments: List<Any>
) : TestTemplateInvocationContext {

    override fun getDisplayName(invocationIndex: Int): String {
        return super.getDisplayName(invocationIndex) + arguments
    }

    override fun getAdditionalExtensions(): List<Extension> {
        return listOf(object : ParameterResolver {
            override fun supportsParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Boolean {
                val declaringExecutable = parameterContext.declaringExecutable
                val testMethod = extensionContext.testMethod.orElse(null)
                return if (declaringExecutable != testMethod) {
                    false
                } else {
                    parameterContext.index < arguments.size
                }
            }

            override fun resolveParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Any {
                return arguments[parameterContext.index]
            }

        })
    }
}
