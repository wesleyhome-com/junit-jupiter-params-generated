package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

internal class GeneratedParametersTestInvocationContext(
    private val namePattern: String,
    private val methodDisplayName: String,
    private val arguments: List<GeneratedArgument>
) : TestTemplateInvocationContext {

    override fun getDisplayName(invocationIndex: Int): String =
        InvocationDisplayNameFormatter.format(namePattern, invocationIndex, arguments, methodDisplayName)

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
            ): Boolean = argumentFor(parameterContext.index) != null

            override fun resolveParameter(
                parameterContext: ParameterContext,
                extensionContext: ExtensionContext
            ): Any? = argumentFor(parameterContext.index)?.value

        })
    }

    private fun argumentFor(parameterIndex: Int): GeneratedArgument? =
        arguments.firstOrNull { it.parameterIndex == parameterIndex }
}
