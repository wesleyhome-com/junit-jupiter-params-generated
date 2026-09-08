package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

/**
 * Doubles as its own [ParameterResolver]. JUnit builds an extension registry per invocation and
 * releases the context afterwards, so nothing is shared between parallel invocations.
 */
internal class GeneratedParametersTestInvocationContext(
    private val template: GeneratedParametersTemplate,
    private val values: Array<Any?>
) : TestTemplateInvocationContext, ParameterResolver {

    private val extensions: List<Extension> = listOf(this)

    override fun getDisplayName(invocationIndex: Int): String =
        template.formatter.format(invocationIndex, values)

    override fun getAdditionalExtensions(): List<Extension> = extensions

    /**
     * Claims only the parameters this extension generated a value for, so `TestInfo`, `@TempDir`,
     * Mockito and Spring injection fall through to the resolver that owns them.
     */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean = template.slotOf(parameterContext) != GeneratedParameterLayout.NO_SLOT

    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any? = values[template.slotOf(parameterContext)]
}
