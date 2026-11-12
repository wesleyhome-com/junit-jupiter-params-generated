package com.wesleyhome.test.jupiter.annotations.validation

/**
 * The function a `@ValuesFrom` names, and the wording for what can be wrong with it.
 *
 * Shared by the extension and the annotation processor, so a reference reads the same way and a
 * problem is described the same way whether it is caught at compile time or when the test runs.
 */
class ValuesFromReference private constructor(
    private val reference: String,
    private val parameter: String,
    /** The class named before `#`, or `null` when the function is looked up on the test class. */
    val className: String?,
    val functionName: String
) {

    private val prefix = "@ValuesFrom(\"$reference\") on [$parameter]"

    fun missingClass(): String = "$prefix: no class named [$className]"

    fun missingFunction(owner: String): String =
        "$prefix: no function named [$functionName] on $owner or its companion object"

    fun instanceFunction(): String =
        "$prefix: [$functionName] must be static or declared in an object or companion object, " +
            "because values are generated before the test instance exists"

    fun takesArguments(count: Int): String = "$prefix: [$functionName] must take no arguments, but takes $count"

    fun wrongReturnType(actual: String): String =
        "$prefix: [$functionName] must return a List, Sequence or array, but returns $actual"

    fun wrongElementType(element: String, expected: String): String =
        "$prefix: [$functionName] returns $element values, but [$parameter] is $expected"

    fun nullableElements(): String =
        "$prefix: [$functionName] may return null, but [$parameter] is not nullable. " +
            "Declare it nullable or annotate it @WithNull"

    fun nullElement(position: Int): String =
        "$prefix: [$functionName] returned null at position $position, but [$parameter] is not nullable"

    fun wrongElement(element: String, position: Int, expected: String): String =
        "$prefix: [$functionName] returned $element at position $position, but [$parameter] is $expected"

    fun threw(cause: Throwable): String =
        "$prefix: [$functionName] threw ${cause.javaClass.simpleName}: ${cause.message}"

    fun tooMany(limit: Long, property: String): String =
        "$prefix: [$functionName] returned more than $limit values, the most one test may generate. " +
            "Return fewer, or raise $property."

    companion object {

        /** Returns `null` when [reference] is neither `name` nor `com.example.Owner#name`. */
        fun parse(reference: String, parameter: String): ValuesFromReference? {
            val parts = reference.split('#')
            return when {
                parts.size == 1 && parts[0].isNotBlank() ->
                    ValuesFromReference(reference, parameter, null, parts[0])
                parts.size == 2 && parts.all { it.isNotBlank() } ->
                    ValuesFromReference(reference, parameter, parts[0], parts[1])
                else -> null
            }
        }

        fun malformed(reference: String, parameter: String): String =
            "@ValuesFrom(\"$reference\") on [$parameter]: expected a function name, " +
                "or a class and a function as \"com.example.Fixtures#values\""
    }
}
