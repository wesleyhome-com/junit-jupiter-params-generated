package com.wesleyhome.test.jupiter

import org.junit.jupiter.api.extension.ExtensionContext

/**
 * What generation publishes as JUnit report entries, which is where structured output belongs -
 * the display name is one string, it gets truncated, and nothing downstream can query it.
 */
object GeneratedParametersReport {

    /** One entry per test method saying how many invocations it produced and from what. */
    const val SUMMARY_PROPERTY: String = "com.wesleyhome.test.jupiter.report.summary"

    /**
     * One entry per invocation carrying its generated values.
     *
     * Off by default: a ten thousand invocation product means ten thousand entries, and the values
     * are already in the display name. Turn it on when the display name is truncated or when
     * something downstream needs to read the values back.
     */
    const val VALUES_PROPERTY: String = "com.wesleyhome.test.jupiter.report.values"

    internal fun summaryEnabled(context: ExtensionContext): Boolean = flag(context, SUMMARY_PROPERTY, true)

    internal fun valuesEnabled(context: ExtensionContext): Boolean = flag(context, VALUES_PROPERTY, false)

    private fun flag(context: ExtensionContext, property: String, default: Boolean): Boolean =
        context.getConfigurationParameter(property)
            .map { raw ->
                raw.trim().toBooleanStrictOrNull()
                    ?: throw IllegalArgumentException("$property must be true or false, but was [$raw]")
            }
            .orElse(default)
}
