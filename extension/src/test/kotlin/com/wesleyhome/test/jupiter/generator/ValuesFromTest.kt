package com.wesleyhome.test.jupiter.generator

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.wesleyhome.test.jupiter.InvalidValuesFromException
import com.wesleyhome.test.jupiter.MAX_PERMUTATIONS_PROPERTY
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy
import com.wesleyhome.test.jupiter.annotations.ValuesFrom
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.java.values.JavaValuesFromFixture
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.provider.ValuesFromDataProvider
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationCounts
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test

class ValuesFromTest {

    @Test
    fun testDataClassValuesCombineWithOtherParameters() {
        assertThat(invocationNames(Fixture::class.java, "withOthers")).isEqualTo(
            listOf("Point(x=0, y=0), 1", "Point(x=0, y=0), 2", "Point(x=1, y=2), 1", "Point(x=1, y=2), 2")
        )
    }

    @Test
    fun testTwoParametersFromOneFunctionFormTheirProduct() {
        assertThat(invocationNames(Fixture::class.java, "pairOfPoints")).isEqualTo(
            listOf(
                "Point(x=0, y=0), Point(x=0, y=0)", "Point(x=0, y=0), Point(x=1, y=2)",
                "Point(x=1, y=2), Point(x=0, y=0)", "Point(x=1, y=2), Point(x=1, y=2)"
            )
        )
    }

    @Test
    fun testNestedDataClasses() {
        assertThat(invocationNames(Fixture::class.java, "nested"))
            .isEqualTo(listOf("Segment(from=Point(x=0, y=0), to=Point(x=1, y=2))"))
    }

    @Test
    fun testValueClassValuesAreUnwrappedForTheCompiledMethod() {
        assertThat(invocationNames(Fixture::class.java, "valueClass")).isEqualTo(listOf("Money(cents=5)", "Money(cents=7)"))
    }

    @Test
    fun testSequenceArrayAndPrimitiveArrayAreAccepted() {
        assertThat(invocationNames(Fixture::class.java, "sequence")).isEqualTo(listOf("Point(x=3, y=4)"))
        assertThat(invocationNames(Fixture::class.java, "array")).isEqualTo(listOf("Point(x=5, y=6)"))
        assertThat(invocationNames(Fixture::class.java, "primitiveArray")).isEqualTo(listOf("8", "9"))
    }

    @Test
    fun testFunctionWithoutJvmStaticOnTheCompanion() {
        assertThat(invocationNames(Fixture::class.java, "notJvmStatic")).isEqualTo(listOf("Point(x=7, y=7)"))
    }

    @Test
    fun testFullyQualifiedFunctionOnAnObject() {
        assertThat(invocationNames(Fixture::class.java, "onAnObject")).isEqualTo(listOf("Point(x=10, y=10)"))
    }

    @Test
    fun testFullyQualifiedTopLevelFunction() {
        assertThat(invocationNames(Fixture::class.java, "topLevel")).isEqualTo(listOf("Point(x=20, y=20)"))
    }

    @Test
    fun testFunctionInheritedFromABaseClass() {
        assertThat(invocationNames(SubclassFixture::class.java, "inherited")).isEqualTo(listOf("Point(x=30, y=30)"))
    }

    @Test
    fun testFilterAppliesToValuesFromAFunction() {
        assertThat(invocationNames(Fixture::class.java, "filtered"))
            .isEqualTo(listOf("Point(x=0, y=0), Point(x=1, y=2)", "Point(x=1, y=2), Point(x=0, y=0)"))
    }

    @Test
    fun testPairwiseSelectsFromValuesFromAFunction() {
        assertThat(invocationCounts(Fixture::class.java, "pairwise")).isEqualTo(4L to 4L)
    }

    @Test
    fun testWithNullAddsANullCase() {
        assertThat(invocationNames(Fixture::class.java, "withNull"))
            .isEqualTo(listOf("Point(x=0, y=0)", "Point(x=1, y=2)", "null"))
    }

    @Test
    fun testNullableElementsIntoANullableParameter() {
        assertThat(invocationNames(Fixture::class.java, "nullableElements"))
            .isEqualTo(listOf("Point(x=0, y=0)", "null", "null"))
    }

    @Test
    fun testMissingFunction() {
        assertFails("missing", "@ValuesFrom(\"nope\") on [point]: no function named [nope] on Fixture or its companion object")
    }

    @Test
    fun testInstanceFunction() {
        assertFails(
            "instance",
            "@ValuesFrom(\"instancePoints\") on [point]: [instancePoints] must be static or declared in an object " +
                "or companion object, because values are generated before the test instance exists"
        )
    }

    @Test
    fun testFunctionTakingArguments() {
        assertFails("takesArguments", "@ValuesFrom(\"pointsFor\") on [point]: [pointsFor] must take no arguments, but takes 1")
    }

    @Test
    fun testFunctionReturningASingleValue() {
        assertFails(
            "singleValue",
            "@ValuesFrom(\"onePoint\") on [point]: [onePoint] must return a List, Sequence or array, but returns Point"
        )
    }

    @Test
    fun testFunctionReturningTheWrongElementType() {
        assertFails("wrongElements", "@ValuesFrom(\"names\") on [point]: [names] returns String values, but [point] is Point")
    }

    @Test
    fun testNullableElementsIntoANonNullParameter() {
        assertFails(
            "nullsIntoNonNull",
            "@ValuesFrom(\"pointsAndNull\") on [point]: [pointsAndNull] may return null, but [point] is not nullable. " +
                "Declare it nullable or annotate it @WithNull"
        )
    }

    /** A generic function declares nothing checkable, so each value is checked as it arrives. */
    @Test
    fun testGenericFunctionReturningTheWrongType() {
        assertFails(
            "genericWrongType",
            "@ValuesFrom(\"anything\") on [point]: [anything] returned String at position 0, but [point] is Point"
        )
    }

    @Test
    fun testGenericFunctionReturningNull() {
        assertFails(
            "genericNull",
            "@ValuesFrom(\"nothingButNull\") on [point]: [nothingButNull] returned null at position 0, but [point] is not nullable"
        )
    }

    @Test
    fun testFunctionReturningNull() {
        assertFails(
            "returnsNull",
            "@ValuesFrom(\"noList\") on [point]: [noList] must return a List, Sequence or array, but returns null"
        )
    }

    @Test
    fun testFunctionThatThrows() {
        val failure = executionFailure(Fixture::class.java, "throws")
        assertThat(failure).isInstanceOf(InvalidValuesFromException::class)
            .hasMessage("@ValuesFrom(\"broken\") on [point]: [broken] threw IllegalStateException: no fixtures today")
        assertThat(failure.cause!!).isInstanceOf(IllegalStateException::class)
    }

    @Test
    fun testMalformedReference() {
        assertFails(
            "malformed",
            "@ValuesFrom(\"a#b#c\") on [point]: expected a function name, or a class and a function as " +
                "\"com.example.Fixtures#values\""
        )
    }

    @Test
    fun testMissingClass() {
        assertFails("missingClass", "@ValuesFrom(\"com.example.Missing#points\") on [point]: no class named [com.example.Missing]")
    }

    @Test
    fun testMoreValuesThanTheCeilingAllows() {
        assertThat(executionFailure(Fixture::class.java, "tooMany", MAX_PERMUTATIONS_PROPERTY to "3"))
            .isInstanceOf(InvalidValuesFromException::class)
            .hasMessage(
                "@ValuesFrom(\"fivePoints\") on [point]: [fivePoints] returned more than 3 values, the most one " +
                    "test may generate. Return fewer, or raise $MAX_PERMUTATIONS_PROPERTY."
            )
    }

    /** Reading stops at the ceiling, so an endless sequence fails instead of hanging the test run. */
    @Test
    fun testEndlessSequenceStopsAtTheCeiling() {
        assertThat(executionFailure(Fixture::class.java, "endless", MAX_PERMUTATIONS_PROPERTY to "3"))
            .isInstanceOf(InvalidValuesFromException::class)
    }

    /** A star projection declares no element type, so each value is checked as it arrives. */
    @Test
    fun testStarProjectedListIsCheckedValueByValue() {
        assertThat(invocationNames(Fixture::class.java, "starProjected")).isEqualTo(listOf("Point(x=0, y=0)"))
    }

    @Test
    fun testFullyQualifiedObjectWithoutTheFunction() {
        assertFails(
            "objectWithoutFunction",
            "@ValuesFrom(\"com.wesleyhome.test.jupiter.generator.SharedPoints#nope\") on [point]: no function named " +
                "[nope] on SharedPoints or its companion object"
        )
    }

    @Test
    fun testJavaStaticMethod() {
        assertThat(invocationNames(JavaValuesFromFixture::class.java, "fromStatic")).isEqualTo(listOf("Point(x=40, y=40)"))
    }

    /** Kotlin reads a raw `List` as a list of `Any`, which is no more a list of points than `List<Object>` is. */
    @Test
    fun testJavaRawListIsRejected() {
        assertThat(executionFailure(JavaValuesFromFixture::class.java, "fromRawList"))
            .isInstanceOf(InvalidValuesFromException::class)
            .hasMessage("@ValuesFrom(\"rawList\") on [point]: [rawList] returns Any values, but [point] is Point")
    }

    @Test
    fun testProviderCannotBeCalledWithoutTheTestClass() {
        val parameter = TestParameter("point", Point::class, annotations = listOf(ValuesFrom("points")))
        assertFailure { ValuesFromDataProvider().createParameterOptionsData(parameter) }
            .isInstanceOf(UnsupportedOperationException::class)
    }

    private fun assertFails(method: String, message: String) {
        assertThat(executionFailure(Fixture::class.java, method))
            .isInstanceOf(InvalidValuesFromException::class)
            .hasMessage(message)
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun withOthers(@ValuesFrom("points") point: Point, @IntSource([1, 2]) n: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun pairOfPoints(@ValuesFrom("points") a: Point, @ValuesFrom("points") b: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun nested(@ValuesFrom("segments") segment: Segment) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun valueClass(@ValuesFrom("amounts") amount: Money) {
            check(amount.cents in setOf(5L, 7L))
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun sequence(@ValuesFrom("pointSequence") point: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun array(@ValuesFrom("pointArray") point: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun primitiveArray(@ValuesFrom("longs") value: Long) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun notJvmStatic(@ValuesFrom("companionOnly") point: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun onAnObject(@ValuesFrom("com.wesleyhome.test.jupiter.generator.SharedPoints#points") point: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun topLevel(@ValuesFrom("com.wesleyhome.test.jupiter.generator.ValuesFromTestKt#topLevelPoints") point: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun filtered(@ValuesFrom("points") a: Point, @ValuesFrom("points") b: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = GenerationStrategy.PAIRWISE)
        fun pairwise(
            @ValuesFrom("points") a: Point,
            @ValuesFrom("points") b: Point,
            @ValuesFrom("points") c: Point
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun withNull(@ValuesFrom("points") @WithNull point: Point?) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun nullableElements(@ValuesFrom("pointsAndNull") point: Point?) {
        }

        @GeneratedParametersTest
        fun missing(@ValuesFrom("nope") point: Point) {
        }

        @GeneratedParametersTest
        fun instance(@ValuesFrom("instancePoints") point: Point) {
        }

        @GeneratedParametersTest
        fun takesArguments(@ValuesFrom("pointsFor") point: Point) {
        }

        @GeneratedParametersTest
        fun singleValue(@ValuesFrom("onePoint") point: Point) {
        }

        @GeneratedParametersTest
        fun wrongElements(@ValuesFrom("names") point: Point) {
        }

        @GeneratedParametersTest
        fun nullsIntoNonNull(@ValuesFrom("pointsAndNull") point: Point) {
        }

        @GeneratedParametersTest
        fun genericWrongType(@ValuesFrom("anything") point: Point) {
        }

        @GeneratedParametersTest
        fun genericNull(@ValuesFrom("nothingButNull") point: Point) {
        }

        @GeneratedParametersTest
        fun returnsNull(@ValuesFrom("noList") point: Point) {
        }

        @GeneratedParametersTest
        fun throws(@ValuesFrom("broken") point: Point) {
        }

        @GeneratedParametersTest
        fun malformed(@ValuesFrom("a#b#c") point: Point) {
        }

        @GeneratedParametersTest
        fun missingClass(@ValuesFrom("com.example.Missing#points") point: Point) {
        }

        @GeneratedParametersTest
        fun tooMany(@ValuesFrom("fivePoints") point: Point) {
        }

        @GeneratedParametersTest
        fun endless(@ValuesFrom("endlessPoints") point: Point) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun starProjected(@ValuesFrom("starPoints") point: Point) {
        }

        @GeneratedParametersTest
        fun objectWithoutFunction(@ValuesFrom("com.wesleyhome.test.jupiter.generator.SharedPoints#nope") point: Point) {
        }

        fun instancePoints(): List<Point> = listOf(Point(0, 0))

        companion object {
            private val ORIGIN = Point(0, 0)
            private val SECOND = Point(1, 2)

            @JvmStatic
            fun points(): List<Point> = listOf(ORIGIN, SECOND)

            @JvmStatic
            fun segments(): List<Segment> = listOf(Segment(ORIGIN, SECOND))

            @JvmStatic
            fun amounts(): List<Money> = listOf(Money(5), Money(7))

            @JvmStatic
            fun pointSequence(): Sequence<Point> = sequenceOf(Point(3, 4))

            @JvmStatic
            fun pointArray(): Array<Point> = arrayOf(Point(5, 6))

            @JvmStatic
            fun longs(): LongArray = longArrayOf(8, 9)

            fun companionOnly(): List<Point> = listOf(Point(7, 7))

            @JvmStatic
            fun filtered_filter(a: Point, b: Point): Boolean = a != b

            @JvmStatic
            fun pointsAndNull(): List<Point?> = listOf(ORIGIN, null)

            @JvmStatic
            fun pointsFor(count: Int): List<Point> = List(count) { ORIGIN }

            @JvmStatic
            fun onePoint(): Point = ORIGIN

            @JvmStatic
            fun names(): List<String> = listOf("origin")

            @Suppress("UNCHECKED_CAST")
            @JvmStatic
            fun <T> anything(): List<T> = listOf("not a point" as T)

            @JvmStatic
            fun <T> nothingButNull(): List<T?> = listOf(null)

            @JvmStatic
            fun noList(): List<Point>? = null

            @JvmStatic
            fun broken(): List<Point> = error("no fixtures today")

            @JvmStatic
            fun starPoints(): List<*> = listOf(ORIGIN)

            @JvmStatic
            fun fivePoints(): List<Point> = List(5) { Point(it, it) }

            @JvmStatic
            fun endlessPoints(): Sequence<Point> = generateSequence(0) { it + 1 }.map { Point(it, it) }
        }
    }

    open class BaseFixture {
        companion object {
            @JvmStatic
            fun inheritedPoints(): List<Point> = listOf(Point(30, 30))
        }
    }

    class SubclassFixture : BaseFixture() {
        @GeneratedParametersTest(name = "{arguments}")
        fun inherited(@ValuesFrom("inheritedPoints") point: Point) {
        }
    }
}

data class Point(val x: Int, val y: Int)

data class Segment(val from: Point, val to: Point)

object SharedPoints {
    fun points(): List<Point> = listOf(Point(10, 10))
}

fun topLevelPoints(): List<Point> = listOf(Point(20, 20))
