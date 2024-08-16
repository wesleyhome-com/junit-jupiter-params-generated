package com.wesleyhome.test.jupiter.annotations

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.create
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.platform.commons.util.AnnotationUtils.isAnnotated
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream
import java.util.stream.StreamSupport

class GeneratedParametersTestExtension : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext): Boolean {
        return if (!context.testMethod.isPresent) false else {
            val method = context.testMethod.get()
            if (isAnnotated(method, GeneratedParametersTest::class.java)) {
                getStore(context).put(METHOD_CONTEXT_KEY, GeneratedParametersTestMethodContext(context))
                true
            } else false
        }
    }

    override fun provideTestTemplateInvocationContexts(extensionContext: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val templateMethod: Method = extensionContext.requiredTestMethod
        println(templateMethod)
        val displayName: String = extensionContext.displayName
        println(displayName)
        val methodContext = getStore(extensionContext).get(METHOD_CONTEXT_KEY, GeneratedParametersTestMethodContext::class.java)
        println(methodContext)
        val arguments = methodContext.generator.arguments()
        val indexCounter = AtomicInteger(1)
        val stream: Stream<TestTemplateInvocationContext> = StreamSupport.stream(arguments.spliterator(), false)
            .map { GeneratedParametersTestInvocationContext(it.get().toList()) }
        return stream
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
