package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.DEFAULT_MAX_PERMUTATIONS
import com.wesleyhome.test.jupiter.MAX_PERMUTATIONS_PROPERTY
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.create
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.jupiter.params.ParameterizedTest
import org.junit.platform.commons.util.Preconditions
import java.util.stream.Stream
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.kotlinFunction

internal class GeneratedParametersTestExtension : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext): Boolean {
        return if (!context.testMethod.isPresent) false else {
            val kFunction = context.requiredTestMethod.kotlinFunction!!
            if (kFunction.hasAnnotation<GeneratedParametersTest>()) {
                val isParameterizedTest = kFunction.hasAnnotation<ParameterizedTest>()
                Preconditions.condition(
                    !isParameterizedTest,
                    "Test annotated with @GeneratedParametersTest cannot be annotated with @ParameterizedTest"
                )
                getStore(context).put(METHOD_CONTEXT_KEY, GeneratedParametersTestMethodContext(context))
                true
            } else false
        }
    }

    override fun provideTestTemplateInvocationContexts(extensionContext: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val methodContext =
            getStore(extensionContext).get(METHOD_CONTEXT_KEY, GeneratedParametersTestMethodContext::class.java)
                ?: return Stream.empty()
        val generator = methodContext.generator
        val layout = generator.layout
        val formatter = InvocationDisplayNameFormatter.compile(
            methodContext.namePattern, extensionContext.displayName, layout
        )
        return generator.arguments(maxPermutations(extensionContext))
            .stream()
            .map { values -> GeneratedParametersTestInvocationContext(formatter, layout, values) }
    }

    private fun maxPermutations(context: ExtensionContext): Long =
        context.getConfigurationParameter(MAX_PERMUTATIONS_PROPERTY)
            .map { raw ->
                raw.trim().toLongOrNull()
                    ?: throw IllegalArgumentException("$MAX_PERMUTATIONS_PROPERTY must be a number, but was [$raw]")
            }
            .orElse(DEFAULT_MAX_PERMUTATIONS)

    private fun getStore(context: ExtensionContext): ExtensionContext.Store {
        return context.getStore(
            create(GeneratedParametersTestExtension::class.java, context.requiredTestMethod)
        )
    }

    companion object {
        private const val METHOD_CONTEXT_KEY: String = "context"

    }
}
