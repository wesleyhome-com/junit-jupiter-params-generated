package com.wesleyhome.test.jupiter.annotations.processor

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.configureKsp
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Test

/** Compiles real sources through the processor, so these are the errors a build reports. */
@OptIn(ExperimentalCompilerApi::class)
class ValuesFromValidatorTest {

    @Test
    fun testCompanionFunctionReturningTheParameterTypeCompiles() {
        assertThat(errorsFor("@ValuesFrom(\"points\") point: Point")).isEmpty()
    }

    @Test
    fun testSequenceArrayAndPrimitiveArrayCompile() {
        assertThat(errorsFor("@ValuesFrom(\"pointSequence\") point: Point")).isEmpty()
        assertThat(errorsFor("@ValuesFrom(\"pointArray\") point: Point")).isEmpty()
        assertThat(errorsFor("@ValuesFrom(\"longs\") value: Long")).isEmpty()
    }

    @Test
    fun testValueClassElementsCompile() {
        assertThat(errorsFor("@ValuesFrom(\"amounts\") amount: Money")).isEmpty()
    }

    @Test
    fun testFullyQualifiedObjectFunctionCompiles() {
        assertThat(errorsFor("@ValuesFrom(\"fixtures.SharedPoints#points\") point: Point")).isEmpty()
    }

    @Test
    fun testFullyQualifiedTopLevelFunctionCompiles() {
        assertThat(errorsFor("@ValuesFrom(\"fixtures.SampleKt#topLevelPoints\") point: Point")).isEmpty()
    }

    @Test
    fun testNullableElementsIntoAWithNullParameterCompile() {
        assertThat(errorsFor("@ValuesFrom(\"pointsAndNull\") @WithNull point: Point?")).isEmpty()
    }

    @Test
    fun testGenericFunctionIsLeftToTheRuntimeCheck() {
        assertThat(errorsFor("@ValuesFrom(\"anything\") point: Point")).isEmpty()
    }

    @Test
    fun testMissingFunction() {
        assertThat(errorsFor("@ValuesFrom(\"nope\") point: Point")).containsExactly(
            "@ValuesFrom(\"nope\") on [point]: no function named [nope] on Sample or its companion object"
        )
    }

    @Test
    fun testInstanceFunction() {
        assertThat(errorsFor("@ValuesFrom(\"instancePoints\") point: Point")).containsExactly(
            "@ValuesFrom(\"instancePoints\") on [point]: [instancePoints] must be static or declared in an object " +
                "or companion object, because values are generated before the test instance exists"
        )
    }

    @Test
    fun testFunctionTakingArguments() {
        assertThat(errorsFor("@ValuesFrom(\"pointsFor\") point: Point")).containsExactly(
            "@ValuesFrom(\"pointsFor\") on [point]: [pointsFor] must take no arguments, but takes 1"
        )
    }

    @Test
    fun testFunctionReturningASingleValue() {
        assertThat(errorsFor("@ValuesFrom(\"onePoint\") point: Point")).containsExactly(
            "@ValuesFrom(\"onePoint\") on [point]: [onePoint] must return a List, Sequence or array, but returns Point"
        )
    }

    @Test
    fun testFunctionReturningTheWrongElementType() {
        assertThat(errorsFor("@ValuesFrom(\"names\") point: Point")).containsExactly(
            "@ValuesFrom(\"names\") on [point]: [names] returns String values, but [point] is Point"
        )
    }

    @Test
    fun testUnderlyingTypeIsNotAValueClass() {
        assertThat(errorsFor("@ValuesFrom(\"longs\") amount: Money")).containsExactly(
            "@ValuesFrom(\"longs\") on [amount]: [longs] returns Long values, but [amount] is Money"
        )
    }

    @Test
    fun testNullableElementsIntoANonNullParameter() {
        assertThat(errorsFor("@ValuesFrom(\"pointsAndNull\") point: Point")).containsExactly(
            "@ValuesFrom(\"pointsAndNull\") on [point]: [pointsAndNull] may return null, but [point] is not " +
                "nullable. Declare it nullable or annotate it @WithNull"
        )
    }

    @Test
    fun testMalformedReference() {
        assertThat(errorsFor("@ValuesFrom(\"a#b#c\") point: Point")).containsExactly(
            "@ValuesFrom(\"a#b#c\") on [point]: expected a function name, or a class and a function as " +
                "\"com.example.Fixtures#values\""
        )
    }

    @Test
    fun testMissingClass() {
        assertThat(errorsFor("@ValuesFrom(\"fixtures.Missing#points\") point: Point")).containsExactly(
            "@ValuesFrom(\"fixtures.Missing#points\") on [point]: no class named [fixtures.Missing]"
        )
    }

    @Test
    fun testFunctionInheritedFromABaseClassCompiles() {
        val source = """
            package fixtures

            import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
            import com.wesleyhome.test.jupiter.annotations.ValuesFrom

            data class Point(val x: Int, val y: Int)

            open class Base {
                companion object {
                    fun inherited(): List<Point> = listOf(Point(0, 0))
                }
            }

            class Sample : Base() {
                @GeneratedParametersTest
                fun test(@ValuesFrom("inherited") point: Point) {}
            }
        """.trimIndent()
        assertThat(errors(source)).isEmpty()
    }

    private fun errorsFor(parameter: String): List<String> = errors(sample(parameter))

    private fun errors(source: String): List<String> {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.kotlin("Sample.kt", source))
            inheritClassPath = true
            configureKsp { symbolProcessorProviders += AnnotationProcessorProvider() }
        }.compile()
        val errors = result.messages.lines()
            .filter { it.startsWith("e: ") && it != "e: Error occurred in KSP, check log for detail" }
            .map { if (it.startsWith(KSP_ERROR)) it.removePrefix(KSP_ERROR).substringAfter(": ") else it }
        check(errors.isNotEmpty() || result.exitCode == KotlinCompilation.ExitCode.OK) { result.messages }
        return errors
    }

    private companion object {
        /** Followed by `<path>:<line>: <message>`. */
        const val KSP_ERROR = "e: [ksp] "
    }

    private fun sample(parameter: String) = """
        package fixtures

        import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
        import com.wesleyhome.test.jupiter.annotations.ValuesFrom
        import com.wesleyhome.test.jupiter.annotations.WithNull

        data class Point(val x: Int, val y: Int)

        @JvmInline
        value class Money(val cents: Long)

        object SharedPoints {
            fun points(): List<Point> = listOf(Point(0, 0))
        }

        fun topLevelPoints(): List<Point> = listOf(Point(0, 0))

        class Sample {
            @GeneratedParametersTest
            fun test($parameter) {}

            fun instancePoints(): List<Point> = listOf(Point(0, 0))

            companion object {
                @JvmStatic fun points(): List<Point> = listOf(Point(0, 0))
                fun pointSequence(): Sequence<Point> = sequenceOf(Point(0, 0))
                fun pointArray(): Array<Point> = arrayOf(Point(0, 0))
                fun longs(): LongArray = longArrayOf(1)
                fun amounts(): List<Money> = listOf(Money(1))
                fun pointsAndNull(): List<Point?> = listOf(null)
                fun pointsFor(count: Int): List<Point> = List(count) { Point(0, 0) }
                fun onePoint(): Point = Point(0, 0)
                fun names(): List<String> = listOf("origin")
                fun <T> anything(): List<T> = emptyList()
            }
        }
    """.trimIndent()
}
