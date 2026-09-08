package com.wesleyhome.test.jupiter.generator

internal class InvocationDisplayNameFormatter private constructor(
    private val segments: List<Segment>
) {

    fun format(invocationIndex: Int, values: Array<Any?>): String {
        val target = StringBuilder()
        for (segment in segments) {
            segment.appendTo(target, invocationIndex, values)
        }
        var length = target.length
        while (length > 0 && target[length - 1].isWhitespace()) {
            length--
        }
        target.setLength(length)
        return target.toString()
    }

    private fun interface Segment {
        fun appendTo(target: StringBuilder, invocationIndex: Int, values: Array<Any?>)
    }

    companion object {
        private val PLACEHOLDER = Regex("""\{(\w+)}""")

        /**
         * `{0}`, `{1}` and so on count generated slots rather than method parameter positions.
         * Resolving that here holds only while the slot count is fixed for a template.
         */
        fun compile(
            pattern: String,
            methodDisplayName: String,
            layout: GeneratedParameterLayout
        ): InvocationDisplayNameFormatter {
            val segments = mutableListOf<Segment>()
            var cursor = 0
            for (match in PLACEHOLDER.findAll(pattern)) {
                if (match.range.first > cursor) {
                    segments += literal(pattern.substring(cursor, match.range.first))
                }
                segments += segmentFor(match.groupValues[1], match.value, methodDisplayName, layout)
                cursor = match.range.last + 1
            }
            if (cursor < pattern.length) {
                segments += literal(pattern.substring(cursor))
            }
            return InvocationDisplayNameFormatter(segments)
        }

        private fun literal(text: String) = Segment { target, _, _ -> target.append(text) }

        private fun segmentFor(
            key: String,
            raw: String,
            methodDisplayName: String,
            layout: GeneratedParameterLayout
        ): Segment = when (key) {
            "index" -> Segment { target, invocationIndex, _ -> target.append(invocationIndex) }
            "displayName" -> Segment { target, _, _ -> target.append(methodDisplayName) }
            "arguments" -> Segment { target, _, values -> appendValues(target, values, layout, false) }
            "argumentsWithNames" -> Segment { target, _, values -> appendValues(target, values, layout, true) }
            else -> {
                val position = key.toIntOrNull()
                if (position != null && position in 0 until layout.size) {
                    Segment { target, _, values -> target.append(values[position].display()) }
                } else {
                    literal(raw)
                }
            }
        }

        private fun appendValues(
            target: StringBuilder,
            values: Array<Any?>,
            layout: GeneratedParameterLayout,
            withNames: Boolean
        ) {
            for (i in values.indices) {
                if (i > 0) {
                    target.append(", ")
                }
                if (withNames) {
                    target.append(layout.nameOf(i)).append('=')
                }
                target.append(values[i].display())
            }
        }

        private fun Any?.display(): String = when (this) {
            null -> "null"
            is Array<*> -> contentDeepToString()
            else -> toString()
        }
    }
}
