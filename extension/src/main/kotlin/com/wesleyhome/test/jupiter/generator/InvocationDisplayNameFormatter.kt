package com.wesleyhome.test.jupiter.generator

internal object InvocationDisplayNameFormatter {

    private val PLACEHOLDER = Regex("""\{(\w+)}""")

    fun format(
        pattern: String,
        invocationIndex: Int,
        arguments: List<GeneratedArgument>,
        methodDisplayName: String
    ): String {
        val formatted = PLACEHOLDER.replace(pattern) { match ->
            when (val key = match.groupValues[1]) {
                "index" -> invocationIndex.toString()
                "displayName" -> methodDisplayName
                "arguments" -> arguments.joinToString(", ") { it.value.display() }
                "argumentsWithNames" -> arguments.joinToString(", ") { "${it.name}=${it.value.display()}" }
                else -> {
                    val position = key.toIntOrNull()
                    if (position != null && position in arguments.indices) {
                        arguments[position].value.display()
                    } else {
                        // Not a placeholder this extension knows about - leave it as the author wrote it.
                        match.value
                    }
                }
            }
        }
        return formatted.trimEnd()
    }

    private fun Any?.display(): String = when (this) {
        null -> "null"
        is Array<*> -> contentDeepToString()
        else -> toString()
    }
}
