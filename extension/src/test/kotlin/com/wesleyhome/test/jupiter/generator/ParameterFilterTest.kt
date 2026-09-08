package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test

/**
 * A Cartesian product generates combinations that are invalid rather than merely uninteresting.
 * Skipping them in the test body still generates, schedules and runs them; filtering removes them
 * before an invocation exists.
 */
class ParameterFilterTest {

    @Test
    fun testCombinationsRejectedByTheFilterNeverRun() {
        assertThat(invocationNames(Fixture::class.java, "ordered")).isEqualTo(
            listOf("1, 1", "1, 2", "1, 3", "2, 2", "2, 3", "3, 3")
        )
    }

    /** Each filter names only the parameters it is about; a combination must satisfy them all. */
    @Test
    fun testSeveralFiltersEachOverTheirOwnParameters() {
        assertThat(invocationNames(Fixture::class.java, "twoRules")).isEqualTo(
            listOf("1, 1, 10, 20", "1, 2, 10, 20", "2, 2, 10, 20")
        )
    }

    @Test
    fun testFilterParametersAreMatchedByNameNotPosition() {
        assertThat(invocationNames(Fixture::class.java, "reversedNames")).isEqualTo(
            listOf("1, 1", "1, 2", "1, 3", "2, 2", "2, 3", "3, 3")
        )
    }

    @Test
    fun testAFilterNamedAfterItsTestNeedsNoAttribute() {
        assertThat(invocationNames(Fixture::class.java, "byConvention")).isEqualTo(
            listOf("1, 1", "1, 2", "2, 2")
        )
    }

    @Test
    fun testSeveralConventionalFiltersAreDisambiguatedBySuffix() {
        assertThat(invocationNames(Fixture::class.java, "twoByConvention")).isEqualTo(listOf("1, 2"))
    }

    /** Conventional and explicit filters combine, so adding one can never silently do nothing. */
    @Test
    fun testConventionalAndExplicitFiltersBothApply() {
        assertThat(invocationNames(Fixture::class.java, "conventionAndExplicit")).isEqualTo(listOf("1, 2"))
    }

    @Test
    fun testAFilterInheritedFromABaseClassApplies() {
        assertThat(invocationNames(SubclassFixture::class.java, "inherited")).isEqualTo(
            listOf("1, 1", "1, 2", "2, 2")
        )
    }

    @Test
    fun testAFilterThatAcceptsNothingNamesTheFiltersThatRejectedEverything() {
        assertThat(executionFailure(Fixture::class.java, "acceptsNothing"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage(
                "All 3 generated combinations were rejected by [never]. " +
                    "Relax a filter, or widen the values the parameters generate."
            )
    }

    @Test
    fun testAMissingFilterIsReported() {
        assertThat(executionFailure(Fixture::class.java, "missingFilter"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("No filter named [noSuchFilter] on ${Fixture::class.java.name}")
    }

    @Test
    fun testAFilterNamingAnUnknownParameterIsReported() {
        assertThat(executionFailure(Fixture::class.java, "unknownParameter"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("Filter [mentionsMissing] declares [nope], which is not a generated parameter of the test")
    }

    @Test
    fun testAFilterThatDoesNotReturnBooleanIsReported() {
        assertThat(executionFailure(Fixture::class.java, "notABoolean"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("Filter [returnsInt] must return Boolean, but returns int")
    }

    /** Filtering happens before the test instance exists, so an instance method cannot be called. */
    @Test
    fun testAnInstanceFilterIsReported() {
        assertThat(executionFailure(Fixture::class.java, "instanceFilter"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage(
                "Filter [instanceRule] must be static or declared in a companion object, because " +
                    "filtering happens before the test instance exists"
            )
    }

    /** Without this check the mismatch surfaces as a bare "argument type mismatch" from invoke. */
    @Test
    fun testAFilterDeclaringTheWrongTypeIsReported() {
        assertThat(executionFailure(Fixture::class.java, "wrongType"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("Filter [expectsString] declares [start] as String, but the generated parameter is Integer")
    }

    /** Reflection would widen an Int into a Long parameter and run the rule on a different value. */
    @Test
    fun testAFilterThatWouldSilentlyWidenIsReported() {
        assertThat(executionFailure(Fixture::class.java, "widening"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("Filter [expectsLong] declares [start] as long, but the generated parameter is Integer")
    }

    @Test
    fun testAPrimitiveFilterParameterCannotTakeAGeneratedNull() {
        assertThat(executionFailure(Fixture::class.java, "nullIntoPrimitive"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage(
                "Filter [expectsPrimitive] declares [start] as int, " +
                    "which cannot hold the null this parameter generates"
            )
    }

    @Test
    fun testASupertypeFilterParameterIsAccepted() {
        assertThat(invocationNames(Fixture::class.java, "supertype")).isEqualTo(listOf("2", "3"))
    }

    @Test
    fun testAFilterReturningNullIsReported() {
        assertThat(executionFailure(Fixture::class.java, "nullReturn"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("Filter [returnsNull] returned null")
    }

    open class BaseFixture {
        companion object {
            @JvmStatic
            fun sharedStartBeforeEnd(start: Int, end: Int): Boolean = start <= end
        }
    }

    class SubclassFixture : BaseFixture() {

        @GeneratedParametersTest(name = "{arguments}", filters = ["sharedStartBeforeEnd"])
        fun inherited(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }
    }

    class Fixture {

        @GeneratedParametersTest(name = "{arguments}", filters = ["startBeforeEnd"])
        fun ordered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startBeforeEnd", "lowBeforeHigh"])
        fun twoRules(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int,
            @IntRangeSource(min = 10, max = 11) low: Int,
            @IntRangeSource(min = 19, max = 20) high: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["endAfterStart"])
        fun reversedNames(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun byConvention(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun twoByConvention(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startBeforeEnd"])
        fun conventionAndExplicit(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["never"])
        fun acceptsNothing(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(filters = ["noSuchFilter"])
        fun missingFilter(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @GeneratedParametersTest(filters = ["mentionsMissing"])
        fun unknownParameter(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @GeneratedParametersTest(filters = ["returnsInt"])
        fun notABoolean(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @GeneratedParametersTest(filters = ["instanceRule"])
        fun instanceFilter(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @GeneratedParametersTest(filters = ["expectsString"])
        fun wrongType(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @GeneratedParametersTest(filters = ["expectsLong"])
        fun widening(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @GeneratedParametersTest(filters = ["expectsPrimitive"])
        fun nullIntoPrimitive(@IntSource([1, 2]) @WithNull start: Int?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["expectsNumber"])
        fun supertype(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(filters = ["returnsNull"])
        fun nullReturn(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        fun instanceRule(start: Int): Boolean = true

        companion object {
            @JvmStatic
            fun startBeforeEnd(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun lowBeforeHigh(low: Int, high: Int): Boolean = low == 10 && high == 20

            @JvmStatic
            fun endAfterStart(end: Int, start: Int): Boolean = start <= end

            @JvmStatic
            fun byConvention_filter(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun twoByConvention_filter_ordered(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun twoByConvention_filter_distinct(start: Int, end: Int): Boolean = start != end

            @JvmStatic
            fun conventionAndExplicit_filter(start: Int, end: Int): Boolean = start != end

            @JvmStatic
            fun never(start: Int): Boolean = false

            @JvmStatic
            fun mentionsMissing(nope: Int): Boolean = true

            @JvmStatic
            fun returnsInt(start: Int): Int = start

            @JvmStatic
            fun expectsString(start: String): Boolean = true

            @JvmStatic
            fun expectsLong(start: Long): Boolean = true

            @JvmStatic
            fun expectsPrimitive(start: Int): Boolean = true

            @JvmStatic
            fun expectsNumber(start: Number): Boolean = start.toInt() > 1

            @JvmStatic
            fun returnsNull(start: Int): Boolean? = null
        }
    }
}
