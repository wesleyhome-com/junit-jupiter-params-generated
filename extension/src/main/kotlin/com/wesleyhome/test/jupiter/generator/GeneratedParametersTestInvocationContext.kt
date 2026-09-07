package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

internal class GeneratedParametersTestInvocationContext(
    private val argumentsByParameterIndex: Map<Int, Any?>
) : TestTemplateInvocationContext {

    override fun getDisplayName(invocationIndex: Int): String {
        return super.getDisplayName(invocationIndex) + argumentsByParameterIndex.values.toList()
    }

    override fun getAdditionalExtensions(): List<Extension> {
        return listOf(object : ParameterResolver {
            /**
             * Only claims the parameters this extension generated a value for. Everything else -
             * `TestInfo`, `@TempDir`, Mockito and Spring injection - falls through to the resolver
             * that actually owns it.
             */
            override fun supportsParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Boolean = argumentsByParameterIndex.containsKey(parameterContext.index)

            override fun resolveParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Any? {
                return argumentsByParameterIndex[parameterContext.index]
            }

        })
    }
}
