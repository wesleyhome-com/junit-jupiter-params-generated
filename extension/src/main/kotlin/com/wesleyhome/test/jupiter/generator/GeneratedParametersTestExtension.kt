package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.DEFAULT_MAX_PERMUTATIONS
import com.wesleyhome.test.jupiter.GeneratedParametersClock
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.GeneratedParametersReport
import com.wesleyhome.test.jupiter.MAX_PERMUTATIONS_PROPERTY
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ExtensionContext.Namespace.create
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.jupiter.params.ParameterizedTest
import org.junit.platform.commons.util.Preconditions
import java.util.stream.Stream
import java.util.stream.StreamSupport
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
        val generator = ParametersGenerator(methodContext.testModel, GeneratedParametersClock.resolve(extensionContext))
        val layout = generator.layout
        val template = GeneratedParametersTemplate(
            extensionContext.requiredTestMethod,
            InvocationDisplayNameFormatter.compile(methodContext.namePattern, extensionContext.displayName, layout),
            layout,
            GeneratedParametersReport.valuesEnabled(extensionContext)
        )
        val arguments = generator.arguments(maxPermutations(extensionContext))
        val filters = ParameterFilter.resolve(
            extensionContext.requiredTestClass, methodContext.filterNames, layout
        )
        if (GeneratedParametersReport.summaryEnabled(extensionContext)) {
            extensionContext.publishReportEntry(summary(generator, arguments, methodContext.filterNames))
        }
        return valuesOf(arguments, filters, methodContext.filterNames)
            .map { values -> GeneratedParametersTestInvocationContext(template, values) }
    }

    /**
     * Unfiltered generation streams straight off the odometer. Filtering goes through a sequence so
     * that exhausting it having emitted nothing can say which filters rejected everything - JUnit
     * otherwise reports only that the provider gave no invocation contexts, and advises overriding
     * a method that means nothing to the author of the test.
     */
    private fun valuesOf(
        arguments: ArgumentParameters,
        filters: List<ParameterFilter>,
        filterNames: Set<String>
    ): Stream<Array<Any?>> {
        if (filters.isEmpty()) {
            return arguments.stream()
        }
        val accepted = sequence {
            var emitted = false
            for (permutation in 0 until arguments.totalPermutations) {
                val values = arguments.valuesAt(permutation)
                if (filters.all { it.accepts(values) }) {
                    emitted = true
                    yield(values)
                }
            }
            if (!emitted) {
                throw InvalidFilterException(
                    "All ${arguments.totalPermutations} generated combinations were rejected by " +
                        filterNames.sorted().joinToString(", ", "[", "]") +
                        ". Relax a filter, or widen the values the parameters generate."
                )
            }
        }
        return StreamSupport.stream(accepted.asIterable().spliterator(), false)
    }

    /**
     * `generated.invocations` counts what was generated, not what ran - filters remove combinations
     * afterwards - so the filters are named when there are any, rather than leaving a reader to
     * wonder why the count and the test tree disagree. A report entry value may not be blank, so a
     * method with no generated parameters omits that key entirely.
     */
    private fun summary(
        generator: ParametersGenerator,
        arguments: ArgumentParameters,
        filterNames: Set<String>
    ): Map<String, String> {
        val summary = mutableMapOf("generated.invocations" to arguments.totalPermutations.toString())
        if (generator.parameters.isNotEmpty()) {
            summary["generated.parameters"] =
                generator.parameters.joinToString(" x ") { "${it.name}=${it.options.size}" }
        }
        if (filterNames.isNotEmpty()) {
            summary["generated.filters"] = filterNames.sorted().joinToString(", ")
        }
        return summary
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
