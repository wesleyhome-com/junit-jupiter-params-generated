package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.messageContains
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.java.filter.JavaFilterFixture
import com.wesleyhome.test.jupiter.java.filter.JavaFilterScopesFixture
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationNames
import com.wesleyhome.test.jupiter.testkit.reportEntries
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.concurrent.atomic.AtomicInteger

/** Counts calls to `duplicated_filter`, to prove naming a filter twice does not apply it twice. */
private val duplicateCalls = AtomicInteger(0)

private val jvmStaticCalls = AtomicInteger(0)

private val ORDERED = listOf("1, 1", "1, 2", "1, 3", "2, 2", "2, 3", "3, 3")

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
    fun testTheBareConventionalFilterAppliesAlongsideSuffixedOnes() {
        assertThat(invocationNames(Fixture::class.java, "bareAndSuffixed")).isEqualTo(listOf("1, 2"))
    }

    @Test
    fun testSeveralConventionalAndSeveralExplicitFiltersAllApply() {
        assertThat(invocationNames(Fixture::class.java, "manyFilters")).isEqualTo(listOf("2, 3"))
    }

    /**
     * A conventional filter belongs to its own test. Nothing else in this class would catch a leak,
     * because every other conventional filter here happens to agree with the explicit ones.
     */
    @Test
    fun testAConventionalFilterDoesNotLeakToASiblingTest() {
        assertThat(invocationNames(Fixture::class.java, "sibling")).isEqualTo(
            listOf("1, 1", "1, 2", "2, 1", "2, 2")
        )
        assertThat(invocationNames(Fixture::class.java, "siblingWithFilter")).isEqualTo(listOf("1, 1", "1, 2"))
    }

    /** Naming a conventional filter explicitly must not apply it twice. */
    @Test
    fun testAFilterNamedBothWaysRunsOnce() {
        duplicateCalls.set(0)
        assertThat(invocationNames(Fixture::class.java, "duplicated")).isEqualTo(
            listOf("1, 1", "1, 2", "2, 2")
        )
        assertThat(duplicateCalls.get()).isEqualTo(4)
    }

    @Test
    fun testAConventionalFilterInheritedFromABaseClassApplies() {
        assertThat(invocationNames(SubclassFixture::class.java, "inheritedByConvention"))
            .isEqualTo(listOf("1, 1", "1, 2", "2, 2"))
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

    /** The lambda compiles to `capturingLambda_filter_small$lambda$0`, taking the captured `$start`. */
    @Test
    fun testALambdaInsideAConventionalFilterIsNotAFilter() {
        assertThat(invocationNames(Fixture::class.java, "capturingLambda")).isEqualTo(listOf("1", "2"))
    }

    /**
     * A lambda or local function whose own parameters happen to bind used to be applied as an extra
     * filter, which changed what ran rather than failing.
     */
    @Test
    fun testALambdaOrLocalFunctionWhoseParametersBindIsNotAppliedAsAFilter() {
        assertThat(invocationNames(Fixture::class.java, "bindingLambda")).isEqualTo(listOf("1", "3"))
        assertThat(invocationNames(Fixture::class.java, "bindingLocalFunction")).isEqualTo(listOf("1", "3"))
    }

    /** The default argument compiles to `withDefault_filter_ordered$default`, which is not a filter. */
    @Test
    fun testAConventionalFilterWithADefaultArgumentApplies() {
        assertThat(invocationNames(Fixture::class.java, "withDefault")).isEqualTo(
            listOf("1, 1", "1, 2", "2, 2")
        )
    }

    /** Tests named `price_filter_*` - `@Test`, `@ParameterizedTest` and the rest - are not filters of `price`. */
    @Test
    fun testATestNamedLikeAConventionalFilterIsNotAFilter() {
        assertThat(invocationNames(Fixture::class.java, "price")).isEqualTo(listOf("1", "3"))
        assertThat(invocationNames(JavaFilterFixture::class.java, "price")).isEqualTo(listOf("1", "3"))
    }

    /**
     * A `@JvmSynthetic` filter is the user's own, so it must not be skipped as compiler-generated. It
     * fails at the moment, because a synthetic method records no parameter names, but it fails loudly.
     */
    @Test
    fun testAJvmSyntheticConventionalFilterIsReportedRatherThanIgnored() {
        assertThat(executionFailure(Fixture::class.java, "jvmSynthetic"))
            .isInstanceOf(InvalidFilterException::class)
            .messageContains("Filter [jvmSynthetic_filter_small]")
    }

    /**
     * Skipping names with a `$` must not skip a declared function: an `internal` one, mangled to
     * `internalSuffixed_filter_small$<module>`, or a Java method named with one.
     */
    @Test
    fun testAConventionalFilterWhoseJvmNameContainsADollarApplies() {
        assertThat(invocationNames(Fixture::class.java, "internalSuffixed")).isEqualTo(listOf("1", "2"))
        assertThat(invocationNames(JavaFilterFixture::class.java, "ordered")).isEqualTo(listOf("1", "2"))
    }

    @Test
    fun testFiltersInANamedCompanionApply() {
        assertThat(invocationNames(NamedCompanionFixture::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(NamedCompanionFixture::class.java, "explicit")).isEqualTo(ORDERED)
    }

    @Test
    fun testFiltersInAPrivateCompanionApply() {
        assertThat(invocationNames(PrivateCompanionFixture::class.java, "ordered")).isEqualTo(ORDERED)
    }

    /** The static bridge and the companion member of a `@JvmStatic` filter are one filter. */
    @Test
    fun testAJvmStaticFilterInANamedCompanionRunsOnce() {
        jvmStaticCalls.set(0)
        assertThat(invocationNames(NamedCompanionFixture::class.java, "jvmStatic")).isEqualTo(listOf("1", "2"))
        assertThat(jvmStaticCalls.get()).isEqualTo(3)
    }

    /** An `internal` function's JVM name is mangled to `<name>$<module>`. */
    @Test
    fun testInternalFiltersApply() {
        assertThat(invocationNames(InternalFixture::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(InternalFixture::class.java, "companionOnly")).isEqualTo(ORDERED)
        assertThat(invocationNames(InternalFixture::class.java, "explicit")).isEqualTo(ORDERED)
    }

    @Test
    fun testAnInternalFilterIsNamedByItsKotlinName() {
        assertThat(reportEntries(Fixture::class.java, "internalSuffixed").single()["generated.filters"])
            .isEqualTo("internalSuffixed_filter_small")
        assertThat(executionFailure(InternalFixture::class.java, "rejectsAll"))
            .hasMessage(
                "All 3 generated combinations were rejected by [rejectsAll_filter]. " +
                    "Relax a filter, or widen the values the parameters generate."
            )
    }

    /** Neither Kotlin nor older Java lets an inner class declare static functions of its own. */
    @Test
    fun testANestedClassUsesFiltersFromItsEnclosingClasses() {
        assertThat(invocationNames(OuterFixture.Inner::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(OuterFixture.Inner::class.java, "explicit")).isEqualTo(ORDERED)
        assertThat(invocationNames(OuterFixture.Inner.Deeper::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(OuterFixture.Inner::class.java, "fromTheOuterSuperclass"))
            .isEqualTo(listOf("1, 1", "1, 2", "2, 2"))
        assertThat(invocationNames(JavaFilterScopesFixture.Inner::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(JavaFilterScopesFixture.Inner::class.java, "explicit")).isEqualTo(ORDERED)
    }

    @Test
    fun testANestedClassPrefersItsOwnHierarchyToItsEnclosingClass() {
        assertThat(invocationNames(OuterFixture.Shadowing::class.java, "shadowed")).isEqualTo(listOf("3"))
    }

    /** A named companion is its class's own, so it comes before anything in a superclass. */
    @Test
    fun testTheNearestClassWinsWhateverItsCompanionIsCalled() {
        assertThat(invocationNames(NamedCompanionFixture::class.java, "shadowed")).isEqualTo(listOf("1"))
    }

    /** Conventional filters are looked for from the class running the test, not the one declaring it. */
    @Test
    fun testASubclassFilterAppliesToAnInheritedTest() {
        assertThat(invocationNames(InheritingFixture::class.java, "inheritedTest")).isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(InheritingFixture::class.java, "inheritedExplicitly")).isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(UnfilteredInheritingFixture::class.java, "inheritedTest"))
            .isEqualTo(listOf("1", "2", "3"))
        assertThat(invocationNames(JavaFilterScopesFixture.Inheriting::class.java, "inherited"))
            .isEqualTo(listOf("2", "3"))
    }

    @Test
    fun testFiltersOnAnInterfaceApplyToItsDefaultTests() {
        assertThat(invocationNames(ImplementingFixture::class.java, "fromInterface")).isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(ImplementingFixture::class.java, "explicitFromInterface"))
            .isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(JavaFilterScopesFixture.Implementing::class.java, "fromInterface"))
            .isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(JavaFilterScopesFixture.Implementing::class.java, "explicitFromInterface"))
            .isEqualTo(listOf("2", "3"))
    }

    open class ShadowBase {
        companion object {
            @JvmStatic
            fun shadowRule(start: Int): Boolean = start == 3
        }
    }

    class NamedCompanionFixture : ShadowBase() {

        @GeneratedParametersTest(name = "{arguments}")
        fun ordered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["explicitRule"])
        fun explicit(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["jvmStatic_filter"])
        fun jvmStatic(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["shadowRule"])
        fun shadowed(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        companion object Rules {
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end

            fun explicitRule(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun jvmStatic_filter(start: Int): Boolean {
                jvmStaticCalls.incrementAndGet()
                return start <= 2
            }

            fun shadowRule(start: Int): Boolean = start == 1
        }
    }

    class PrivateCompanionFixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun ordered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        private companion object {
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end
        }
    }

    class InternalFixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun ordered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun companionOnly(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["internalRule"])
        fun explicit(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun rejectsAll(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        companion object {
            @JvmStatic
            internal fun ordered_filter(start: Int, end: Int): Boolean = start <= end

            internal fun companionOnly_filter(start: Int, end: Int): Boolean = start <= end

            internal fun internalRule(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            internal fun rejectsAll_filter(start: Int): Boolean = false
        }
    }

    class OuterFixture : BaseFixture() {

        @Nested
        inner class Inner {

            @GeneratedParametersTest(name = "{arguments}")
            fun ordered(
                @IntRangeSource(min = 1, max = 3) start: Int,
                @IntRangeSource(min = 1, max = 3) end: Int
            ) {
            }

            @GeneratedParametersTest(name = "{arguments}", filters = ["outerOrdered"])
            fun explicit(
                @IntRangeSource(min = 1, max = 3) start: Int,
                @IntRangeSource(min = 1, max = 3) end: Int
            ) {
            }

            @GeneratedParametersTest(name = "{arguments}", filters = ["sharedStartBeforeEnd"])
            fun fromTheOuterSuperclass(
                @IntRangeSource(min = 1, max = 2) start: Int,
                @IntRangeSource(min = 1, max = 2) end: Int
            ) {
            }

            @Nested
            inner class Deeper {
                @GeneratedParametersTest(name = "{arguments}")
                fun ordered(
                    @IntRangeSource(min = 1, max = 3) start: Int,
                    @IntRangeSource(min = 1, max = 3) end: Int
                ) {
                }
            }
        }

        @Nested
        inner class Shadowing : ShadowBase() {
            @GeneratedParametersTest(name = "{arguments}", filters = ["shadowRule"])
            fun shadowed(@IntRangeSource(min = 1, max = 3) start: Int) {
            }
        }

        companion object {
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun outerOrdered(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun shadowRule(start: Int): Boolean = start == 2
        }
    }

    abstract class InheritedTestBase {

        @GeneratedParametersTest(name = "{arguments}")
        fun inheritedTest(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["notOne"])
        fun inheritedExplicitly(@IntRangeSource(min = 1, max = 3) start: Int) {
        }
    }

    class InheritingFixture : InheritedTestBase() {
        companion object {
            @JvmStatic
            fun inheritedTest_filter(start: Int): Boolean = start != 1

            @JvmStatic
            fun notOne(start: Int): Boolean = start != 1
        }
    }

    /** Shares [InheritedTestBase] with [InheritingFixture], whose filter must not reach it. */
    class UnfilteredInheritingFixture : InheritedTestBase()

    interface DefaultTestContract {

        @GeneratedParametersTest(name = "{arguments}")
        fun fromInterface(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["keepBig"])
        fun explicitFromInterface(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        companion object {
            fun fromInterface_filter(start: Int): Boolean = start >= 2

            fun keepBig(start: Int): Boolean = start >= 2
        }
    }

    class ImplementingFixture : DefaultTestContract

    open class BaseFixture {
        companion object {
            @JvmStatic
            fun sharedStartBeforeEnd(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun inheritedByConvention_filter(start: Int, end: Int): Boolean = start <= end
        }
    }

    class SubclassFixture : BaseFixture() {

        @GeneratedParametersTest(name = "{arguments}", filters = ["sharedStartBeforeEnd"])
        fun inherited(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun inheritedByConvention(
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

        @GeneratedParametersTest(name = "{arguments}")
        fun bareAndSuffixed(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startBeforeEnd", "endIsOdd"])
        fun manyFilters(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        /** No filter of its own; a sibling's conventional filter must not reach it. */
        @GeneratedParametersTest(name = "{arguments}")
        fun sibling(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun siblingWithFilter(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["duplicated_filter"])
        fun duplicated(
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

        @GeneratedParametersTest(name = "{arguments}")
        fun capturingLambda(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun bindingLambda(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun bindingLocalFunction(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun withDefault(
            @IntRangeSource(min = 1, max = 2) start: Int,
            @IntRangeSource(min = 1, max = 2) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun price(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @Test
        fun price_filter_applies() {
        }

        @GeneratedParametersTest
        fun price_filter_generated(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

        @ParameterizedTest
        @ValueSource(ints = [1])
        fun price_filter_parameterized(start: Int) {
        }

        @RepeatedTest(1)
        fun price_filter_repeated() {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun internalSuffixed(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun jvmSynthetic(@IntRangeSource(min = 1, max = 3) start: Int) {
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
            fun bareAndSuffixed_filter(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun bareAndSuffixed_filter_distinct(start: Int, end: Int): Boolean = start != end

            @JvmStatic
            fun manyFilters_filter_startIsEven(start: Int): Boolean = start % 2 == 0

            @JvmStatic
            fun manyFilters_filter_endAboveTwo(end: Int): Boolean = end > 2

            @JvmStatic
            fun endIsOdd(end: Int): Boolean = end % 2 == 1

            /** Belongs to siblingWithFilter only; `sibling` must be unaffected by it. */
            @JvmStatic
            fun siblingWithFilter_filter(start: Int): Boolean = start == 1

            @JvmStatic
            fun duplicated_filter(start: Int, end: Int): Boolean {
                duplicateCalls.incrementAndGet()
                return start <= end
            }

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

            @JvmStatic
            fun capturingLambda_filter_small(start: Int): Boolean {
                val isSmall = { start <= 2 }
                return isSmall()
            }

            @JvmStatic
            fun bindingLambda_filter_oneOrThree(start: Int): Boolean {
                val isOne: (Int) -> Boolean = { start -> start == 1 }
                return isOne(start) || start == 3
            }

            @JvmStatic
            fun bindingLocalFunction_filter_oneOrThree(start: Int): Boolean {
                fun isOne(start: Int) = start == 1
                return isOne(start) || start == 3
            }

            @JvmStatic
            fun withDefault_filter_ordered(start: Int, end: Int = 2): Boolean = start <= end

            @JvmStatic
            fun price_filter_odd(start: Int): Boolean = start % 2 == 1

            @JvmStatic
            internal fun internalSuffixed_filter_small(start: Int): Boolean = start <= 2

            @JvmStatic
            @JvmSynthetic
            fun jvmSynthetic_filter_small(start: Int): Boolean = start <= 2
        }
    }
}
