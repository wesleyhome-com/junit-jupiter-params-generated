package com.wesleyhome.test.jupiter.generator

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.InvocationInterceptor
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.ReflectiveInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.platform.commons.support.ReflectionSupport
import java.lang.reflect.Method

/**
 * Doubles as its own [ParameterResolver], reporter and [InvocationInterceptor]. JUnit builds an
 * extension registry per invocation and releases the context afterwards, so nothing is shared
 * between parallel invocations.
 */
internal class GeneratedParametersTestInvocationContext(
    private val template: GeneratedParametersTemplate,
    private val values: Array<Any?>
) : TestTemplateInvocationContext, ParameterResolver, BeforeTestExecutionCallback, InvocationInterceptor {

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
    ): Any? = template.resolve(parameterContext, values)

    override fun beforeTestExecution(context: ExtensionContext) {
        if (template.reportValues) {
            context.publishReportEntry(template.valuesByName(values))
        }
    }

    /**
     * Registered after every other extension of the invocation, so no other interceptor sits between
     * this one and the call it replaces.
     */
    override fun interceptTestTemplateMethod(
        invocation: InvocationInterceptor.Invocation<Void?>,
        invocationContext: ReflectiveInvocationContext<Method>,
        extensionContext: ExtensionContext
    ) {
        val arguments = template.compiledArguments(invocationContext.arguments)
        if (arguments == null) {
            invocation.proceed()
        } else {
            invocation.skip()
            ReflectionSupport.invokeMethod(invocationContext.executable, invocationContext.target.orElse(null), *arguments)
        }
    }
}
