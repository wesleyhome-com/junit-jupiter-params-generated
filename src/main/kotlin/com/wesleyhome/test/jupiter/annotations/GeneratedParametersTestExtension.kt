package com.wesleyhome.test.jupiter.annotations

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.create
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.jupiter.params.ParameterizedTest
import org.junit.platform.commons.util.AnnotationUtils.isAnnotated
import org.junit.platform.commons.util.Preconditions
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream
import java.util.stream.StreamSupport

class GeneratedParametersTestExtension : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext): Boolean {
        return if (!context.testMethod.isPresent) false else {
            val method = context.testMethod.get()
            if (isAnnotated(method, GeneratedParametersTest::class.java)) {
                Preconditions.condition(
                    !isAnnotated(method, ParameterizedTest::class.java),
                    "Test annotated with @GeneratedParametersTest cannot be annotated with @ParameterizedTest"
                )
                getStore(context).put(METHOD_CONTEXT_KEY, GeneratedParametersTestMethodContext(context))
                true
            } else false
        }
    }

    override fun provideTestTemplateInvocationContexts(extensionContext: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val methodContext = getStore(extensionContext).get(METHOD_CONTEXT_KEY, GeneratedParametersTestMethodContext::class.java)
        val arguments = methodContext.generator.arguments()
        val invocationCount = AtomicInteger(0)
        return StreamSupport.stream(arguments.spliterator(), false)
            .map {
                invocationCount.incrementAndGet()
                GeneratedParametersTestInvocationContext(it.get().toList())
            }
    }

    private fun getStore(context: ExtensionContext): ExtensionContext.Store {
        return context.getStore(
            create(GeneratedParametersTestExtension::class.java, context.requiredTestMethod)
        )
    }

    companion object {
        private const val METHOD_CONTEXT_KEY: String = "context"

    }
}
