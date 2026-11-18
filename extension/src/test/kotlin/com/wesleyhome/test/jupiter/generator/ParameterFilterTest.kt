package com.wesleyhome.test.jupiter.generator

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.annotations.number.LongSource
import com.wesleyhome.test.jupiter.java.filter.JavaCompanionFieldFixture
import com.wesleyhome.test.jupiter.java.filter.JavaFilterFixture
import com.wesleyhome.test.jupiter.java.filter.JavaFilterScopesFixture
import com.wesleyhome.test.jupiter.java.filter.JavaNullableFilters
import com.wesleyhome.test.jupiter.testkit.executeFixture
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationCounts
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

private val compiledNameCalls = AtomicInteger(0)

private val ORDERED = listOf("1, 1", "1, 2", "1, 3", "2, 2", "2, 3", "3, 3")

private val ALL_PAIRS = listOf("1, 1", "1, 2", "1, 3", "2, 1", "2, 2", "2, 3", "3, 1", "3, 2", "3, 3")

/** What the compiler appends to this module's `internal` names. */
internal const val TEST_MODULE = "com_wesleyhome_test_junit_jupiter_params_generated_test"

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
     * A `@JvmSynthetic` filter is the user's own, so it must not be skipped as compiler-generated. A
     * synthetic method records no parameter names for Java reflection, but its Kotlin declaration does.
     */
    @Test
    fun testAJvmSyntheticConventionalFilterApplies() {
        assertThat(invocationNames(Fixture::class.java, "jvmSynthetic")).isEqualTo(listOf("1", "2"))
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
        assertThat(invocationNames(OuterFixture.Inner.Deeper::class.java, "deeper")).isEqualTo(ORDERED)
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

    /** An enclosing class's `<test>_filter` belongs to its own test of that name, inherited or not. */
    @Test
    fun testAnEnclosingFilterForATestOfTheSameNameStaysWithThatTest() {
        assertThat(invocationNames(SameNameOuterFixture::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(SameNameOuterFixture.Inner::class.java, "ordered")).isEqualTo(ALL_PAIRS)
        assertThat(invocationNames(SameNameOuterFixture.Inner::class.java, "parse")).isEqualTo(listOf("1", "2", "3"))
        assertThat(invocationNames(InheritedSameNameOuterFixture.Inner::class.java, "ordered")).isEqualTo(ALL_PAIRS)
    }

    /** A class between the test and the filter that has a test of that name claims the filter too. */
    @Test
    fun testAnEnclosingFilterStopsAtAnIntermediateTestOfTheSameName() {
        assertThat(invocationNames(ThreeLevelFixture.Middle::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(ThreeLevelFixture.Middle.Deep::class.java, "ordered")).isEqualTo(ALL_PAIRS)
    }

    /** Only an inner class can be `@Nested`; a static nested class can declare a companion of its own. */
    @Test
    fun testAStaticNestedClassDoesNotSearchItsEnclosingClass() {
        assertThat(invocationNames(StaticOuterFixture.StaticNested::class.java, "ordered")).isEqualTo(ALL_PAIRS)
        assertThat(executionFailure(StaticOuterFixture.StaticNested::class.java, "explicit"))
            .hasMessage("No filter named [outerOrdered] on ${StaticOuterFixture.StaticNested::class.java.name}")
    }

    @Test
    fun testASuperclassComesBeforeTheTestClassesInterfaces() {
        assertThat(invocationNames(PrecedenceFixture::class.java, "superclassFirst")).isEqualTo(listOf("1"))
    }

    @Test
    fun testFiltersOnASuperclassesInterfaceApply() {
        assertThat(invocationNames(ThroughSuperclassFixture::class.java, "throughSuperclass")).isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(ThroughSuperclassFixture::class.java, "explicitThroughSuperclass"))
            .isEqualTo(listOf("2", "3"))
    }

    /**
     * An `internal` overload compiles to `<name>$<module>` and was never a candidate before, so the
     * public overload it would now tie with is still chosen.
     */
    @Test
    fun testAPublicOverloadIsChosenOverAnInternalOne() {
        assertThat(invocationNames(OverloadFixture::class.java, "conventional")).isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(OverloadFixture::class.java, "conventionalWithBridge")).isEqualTo(listOf("2", "3"))
        assertThat(invocationNames(OverloadFixture::class.java, "explicit")).isEqualTo(listOf("2", "3"))
    }

    /** A value class mangles the name too, as `vc_filter-<hash>$<module>`, and that form was always found. */
    @Test
    fun testAnInternalFilterTakingAValueClassIsChosenAsBefore() {
        val oneAndThree = listOf("Money(cents=1)", "Money(cents=3)")
        assertThat(invocationNames(OverloadFixture::class.java, "valueClassAndPublic")).isEqualTo(oneAndThree)
        assertThat(invocationNames(OverloadFixture::class.java, "valueClassAndInternal")).isEqualTo(oneAndThree)
    }

    @Test
    fun testAnInternalFilterCanBeNamedByItsCompiledName() {
        assertThat(invocationNames(InternalFixture::class.java, "compiledName")).isEqualTo(listOf("2", "3"))
        assertThat(reportEntries(InternalFixture::class.java, "compiledName").single()["generated.filters"])
            .isEqualTo("compiledRule")
    }

    @Test
    fun testAnInternalFilterNamedBothWaysRunsOnce() {
        compiledNameCalls.set(0)
        assertThat(invocationNames(InternalFixture::class.java, "bothNames")).isEqualTo(listOf("1", "2"))
        assertThat(compiledNameCalls.get()).isEqualTo(3)
    }

    @Test
    fun testAnInternalFilterNamedBothWaysIsReportedOnce() {
        assertThat(reportEntries(InternalFixture::class.java, "bothNames").single()["generated.filters"])
            .isEqualTo("bothNames_filter_small")
        assertThat(reportEntries(InternalFixture::class.java, "explicitBothNames").single()["generated.filters"])
            .isEqualTo("bothNamesRule")
    }

    /**
     * An inner class can't have a companion object, so an instance `<test>_filter` there is an easy
     * mistake. It fails as it always has, rather than falling through to an enclosing test's filter.
     */
    @Test
    fun testAnInstanceFilterInANestedClassIsReportedRatherThanReplaced() {
        val mustBeStatic = "Filter [t_filter] must be static or declared in a companion object, because " +
            "filtering happens before the test instance exists"
        assertThat(executionFailure(MisplacedFilterFixture.Inner::class.java, "t")).hasMessage(mustBeStatic)
        assertThat(executionFailure(MisplacedFilterFixture.Middle.Deep::class.java, "t")).hasMessage(mustBeStatic)
    }

    /** Naming a filter is how a `@Nested` test uses one its enclosing class's same-named test claims. */
    @Test
    fun testAnExplicitFilterIsFoundInAnEnclosingClassThatClaimsItsConventionalOne() {
        assertThat(invocationNames(SameNameOuterFixture.ExplicitInner::class.java, "ordered")).isEqualTo(ORDERED)
    }

    @Test
    fun testAMissingFilterOnANestedTestNamesTheNestedClass() {
        assertThat(executionFailure(OuterFixture.Inner::class.java, "missing"))
            .hasMessage("No filter named [nope] on ${OuterFixture.Inner::class.java.name}")
    }

    /** As before explicit filters were resolved separately, so a conventional guard protects them. */
    @Test
    fun testConventionalFiltersRunBeforeExplicitOnes() {
        assertThat(invocationNames(Fixture::class.java, "guarded")).isEqualTo(listOf("1", "2"))
    }

    @Test
    fun testTheFiltersThatRejectedEverythingAreListedInOrder() {
        assertThat(executionFailure(Fixture::class.java, "zeta")).hasMessage(
            "All 3 generated combinations were rejected by [alpha, zeta_filter]. " +
                "Relax a filter, or widen the values the parameters generate."
        )
    }

    /** One declared name, two functions: naming each applies each, as it did before. */
    @Test
    fun testTwoFunctionsWithOneDeclaredNameBothApply() {
        assertThat(invocationNames(OverloadFixture::class.java, "bothOverloads"))
            .isEqualTo(listOf("2, 2", "2, 3", "3, 3"))
    }

    /** The subclass's `internal` filter is the nearer of two with one name, so it replaces the base class's. */
    @Test
    fun testASubclassInternalFilterReplacesABaseFilterOfTheSameName() {
        assertThat(invocationNames(ReplacingFixture::class.java, "replaced")).isEqualTo(listOf("1", "2"))
    }

    /** A Java class has no companion object, but a static field named `Companion` has long served as one. */
    @Test
    fun testAJavaStaticFieldNamedCompanionIsSearched() {
        assertThat(invocationNames(JavaCompanionFieldFixture::class.java, "ordered")).isEqualTo(ORDERED)
        assertThat(invocationNames(JavaCompanionFieldFixture::class.java, "explicit")).isEqualTo(ORDERED)
    }

    /** It used to escape as a bare `InvocationTargetException: null`, naming neither the filter nor the values. */
    @Test
    fun testAThrowingFilterIsNamedWithTheCombinationItThrewOn() {
        for (method in listOf("cartesian", "pairwise")) {
            val failure = executionFailure(ThrowingFixture::class.java, method)
            assertThat(failure).isInstanceOf(InvalidFilterException::class)
                .hasMessage("Filter [throwsAtTwo] on [start=2, end=1] threw IllegalStateException: boom at start=2")
            assertThat(failure.cause).isNotNull().isInstanceOf(IllegalStateException::class).hasMessage("boom at start=2")
        }
    }

    /**
     * Either way the test's container fails, not an invocation. CARTESIAN filters as it runs, so the
     * invocations before the throwing combination have passed. PAIRWISE has to see every combination
     * before it chooses any, so nothing has run.
     */
    @Test
    fun testAThrowingFilterFailsTheContainerWhenTheStrategyReachesTheCombination() {
        val cartesian = executeFixture(ThrowingFixture::class.java, "cartesian")
        assertThat(cartesian.testEvents().succeeded().list().map { it.testDescriptor.displayName })
            .isEqualTo(listOf("1, 1", "1, 2"))
        assertThat(cartesian.testEvents().failed().count()).isEqualTo(0L)
        assertThat(cartesian.containerEvents().failed().count()).isEqualTo(1L)
        val pairwise = executeFixture(ThrowingFixture::class.java, "pairwise")
        assertThat(pairwise.testEvents().started().count()).isEqualTo(0L)
        assertThat(pairwise.containerEvents().failed().count()).isEqualTo(1L)
    }

    @Test
    fun testTheCombinationShowsAGeneratedNullAsDisplayNamesDo() {
        assertThat(executionFailure(ThrowingFixture::class.java, "onNull"))
            .hasMessage("Filter [throwsOnNull] on [start=1, label=null] threw IllegalStateException: no label")
    }

    /** An assertion failing inside a filter is the filter failing, so it is named like any other. */
    @Test
    fun testAnAssertionErrorThrownByAFilterIsWrapped() {
        val failure = executionFailure(ThrowingFixture::class.java, "asserting")
        assertThat(failure).isInstanceOf(InvalidFilterException::class)
            .hasMessage("Filter [assertsSmall] on [start=3] threw AssertionError: start is 3")
        assertThat(failure.cause).isNotNull().isInstanceOf(AssertionError::class).hasMessage("start is 3")
    }

    /** JUnit stops the whole run on an OutOfMemoryError, which wrapping would turn into one failed test. */
    @Test
    fun testAnOutOfMemoryErrorThrownByAFilterIsRethrownAsItIs() {
        val layout = GeneratedParameterLayout(arrayOf("start"), arrayOf(Int::class), booleanArrayOf(false), intArrayOf(0))
        val filter = ParameterFilter.resolve(listOf(ThrowingFixture::class.java), listOf("outOfMemory"), layout).single()
        assertFailure { filter.accepts(arrayOf(1)) }.isInstanceOf(OutOfMemoryError::class).hasMessage("simulated")
    }

    /** It used to run until the null reached the filter, then fail with a NullPointerException from inside it. */
    @Test
    fun testANonNullKotlinFilterParameterCannotTakeAGeneratedNull() {
        for ((method, filter) in listOf(
            "nullIntoNonNull" to "nullIntoNonNull_filter",
            "nullIntoNonNullPairwise" to "nullIntoNonNullPairwise_filter",
            "nullableType" to "notB"
        )) {
            assertThat(executionFailure(NullFixture::class.java, method))
                .isInstanceOf(InvalidFilterException::class)
                .hasMessage(
                    "Filter [$filter] declares [label] as String, which cannot hold the null this parameter generates"
                )
            assertThat(invocationCounts(NullFixture::class.java, method)).isEqualTo(0L to 0L)
        }
    }

    /** A filter that turns the nulls away runs first, so the one behind it never sees a null, and passes as before. */
    @Test
    fun testANonNullFilterParameterBehindAFilterOfTheSameParameterIsAccepted() {
        assertThat(invocationNames(NullFixture::class.java, "guarded")).isEqualTo(listOf("a"))
        assertThat(invocationNames(NullFixture::class.java, "guardedPairwise")).isEqualTo(listOf("1, a", "2, a"))
    }

    /** Named filters run in the order they are listed, and a guard listed after the filter is too late. */
    @Test
    fun testAGuardProtectsOnlyTheFiltersAfterIt() {
        assertThat(invocationNames(NullFixture::class.java, "guardFirst")).isEqualTo(listOf("a"))
        assertThat(executionFailure(NullFixture::class.java, "guardSecond")).hasMessage(
            "Filter [startsWithA] declares [label] as String, which cannot hold the null this parameter generates"
        )
    }

    /** Only a filter that takes the parameter can keep its null away. */
    @Test
    fun testAFilterOfAnotherParameterDoesNotGuardTheNull() {
        assertThat(executionFailure(NullFixture::class.java, "guardedElsewhere")).hasMessage(
            "Filter [startsWithA] declares [label] as String, which cannot hold the null this parameter generates"
        )
        assertThat(executionFailure(NullFixture::class.java, "oneOfTwoGuarded")).hasMessage(
            "Filter [bothNonNull] declares [second] as String, which cannot hold the null this parameter generates"
        )
    }

    @Test
    fun testANullAGuardLetsThroughIsReportedAsTheFilterThrowing() {
        assertThat(executionFailure(NullFixture::class.java, "leakyGuard"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage(
                "Filter [startsWithA] on [label=null] threw NullPointerException: Parameter specified as non-null " +
                    "is null: method ${NullFixture::class.java.name}\$Companion.startsWithA, parameter label"
            )
    }

    /** Kotlin reflection gives a Java parameter a platform type, which isn't marked nullable but takes null. */
    @Test
    fun testFilterParametersThatCanHoldNullStillTakeIt() {
        assertThat(invocationNames(NullFixture::class.java, "nullable")).isEqualTo(listOf("a", "null"))
        assertThat(invocationNames(NullFixture::class.java, "generic")).isEqualTo(listOf("a", "null"))
        assertThat(invocationNames(KotlinTestJavaFilterFixture::class.java, "label")).isEqualTo(listOf("a", "null"))
        assertThat(invocationNames(JavaFilterFixture::class.java, "nullable")).isEqualTo(listOf("a", "null"))
    }

    /**
     * A value class's nullability is its Kotlin type's, whatever it compiles to. `Name` over `String`
     * compiles to a `String` but rejects a null, and `Nickname` over `String?` took the null as
     * `Nickname(null)`, where the test receives null.
     */
    @Test
    fun testANonNullValueClassFilterParameterCannotTakeAGeneratedNull() {
        assertThat(executionFailure(ValueClassNullFixture::class.java, "name")).hasMessage(
            "Filter [nonNullName] declares [name] as Name, which cannot hold the null this parameter generates"
        )
        assertThat(executionFailure(ValueClassNullFixture::class.java, "nickname")).hasMessage(
            "Filter [nonNullNickname] declares [nickname] as Nickname, which cannot hold the null this parameter generates"
        )
    }

    /** Each filter accepts only null, so a box around null would leave nothing to run. */
    @Test
    fun testANullableValueClassFilterParameterTakesTheNull() {
        assertThat(invocationNames(ValueClassNullFixture::class.java, "nullableName")).isEqualTo(listOf("null"))
        assertThat(invocationNames(ValueClassNullFixture::class.java, "nullableMoney")).isEqualTo(listOf("null"))
        assertThat(invocationNames(ValueClassNullFixture::class.java, "nullableNickname")).isEqualTo(listOf("null"))
    }

    class SameNameOuterFixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun ordered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun parse(@IntRangeSource(min = 1, max = 3) a: Int) {
        }

        @Nested
        inner class Inner {
            @GeneratedParametersTest(name = "{arguments}")
            fun ordered(
                @IntRangeSource(min = 1, max = 3) start: Int,
                @IntRangeSource(min = 1, max = 3) end: Int
            ) {
            }

            @GeneratedParametersTest(name = "{arguments}")
            fun parse(@IntRangeSource(min = 1, max = 3) b: Int) {
            }
        }

        @Nested
        inner class ExplicitInner {
            @GeneratedParametersTest(name = "{arguments}", filters = ["ordered_filter"])
            fun ordered(
                @IntRangeSource(min = 1, max = 3) start: Int,
                @IntRangeSource(min = 1, max = 3) end: Int
            ) {
            }
        }

        companion object {
            @JvmStatic
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            fun parse_filter_small(a: Int): Boolean = a <= 2
        }
    }

    abstract class OrderedTestBase {
        @GeneratedParametersTest(name = "{arguments}")
        fun ordered(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }
    }

    class InheritedSameNameOuterFixture : OrderedTestBase() {

        @Nested
        inner class Inner {
            @GeneratedParametersTest(name = "{arguments}")
            fun ordered(
                @IntRangeSource(min = 1, max = 3) start: Int,
                @IntRangeSource(min = 1, max = 3) end: Int
            ) {
            }
        }

        companion object {
            @JvmStatic
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end
        }
    }

    class ThreeLevelFixture {

        @Nested
        inner class Middle {
            @GeneratedParametersTest(name = "{arguments}")
            fun ordered(
                @IntRangeSource(min = 1, max = 3) start: Int,
                @IntRangeSource(min = 1, max = 3) end: Int
            ) {
            }

            @Nested
            inner class Deep {
                @GeneratedParametersTest(name = "{arguments}")
                fun ordered(
                    @IntRangeSource(min = 1, max = 3) start: Int,
                    @IntRangeSource(min = 1, max = 3) end: Int
                ) {
                }
            }
        }

        companion object {
            @JvmStatic
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end
        }
    }

    class MisplacedFilterFixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun t(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @Nested
        inner class Inner {
            @GeneratedParametersTest(name = "{arguments}")
            fun t(@IntRangeSource(min = 1, max = 3) start: Int) {
            }

            fun t_filter(start: Int): Boolean = start != 1
        }

        @Nested
        inner class Middle {
            fun t_filter(start: Int): Boolean = start != 1

            @Nested
            inner class Deep {
                @GeneratedParametersTest(name = "{arguments}")
                fun t(@IntRangeSource(min = 1, max = 3) start: Int) {
                }
            }
        }

        companion object {
            @JvmStatic
            fun t_filter(start: Int): Boolean = start != 1
        }
    }

    class StaticOuterFixture {

        class StaticNested {
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
        }

        companion object {
            fun ordered_filter(start: Int, end: Int): Boolean = start <= end

            fun outerOrdered(start: Int, end: Int): Boolean = start <= end
        }
    }

    open class PrecedenceBase {
        companion object {
            @JvmStatic
            fun precedenceRule(start: Int): Boolean = start == 1
        }
    }

    interface PrecedenceContract {
        companion object {
            fun precedenceRule(start: Int): Boolean = start == 3
        }
    }

    class PrecedenceFixture : PrecedenceBase(), PrecedenceContract {
        @GeneratedParametersTest(name = "{arguments}", filters = ["precedenceRule"])
        fun superclassFirst(@IntRangeSource(min = 1, max = 3) start: Int) {
        }
    }

    interface InheritedContract {
        companion object {
            fun throughSuperclass_filter(start: Int): Boolean = start >= 2

            fun inheritedContractRule(start: Int): Boolean = start >= 2
        }
    }

    open class ContractBase : InheritedContract

    class ThroughSuperclassFixture : ContractBase() {
        @GeneratedParametersTest(name = "{arguments}")
        fun throughSuperclass(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["inheritedContractRule"])
        fun explicitThroughSuperclass(@IntRangeSource(min = 1, max = 3) start: Int) {
        }
    }

    class OverloadFixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun conventional(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun conventionalWithBridge(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["mixRule"])
        fun explicit(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun valueClassAndPublic(@LongSource([1, 2, 3]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun valueClassAndInternal(@LongSource([1, 2, 3]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["mixed", "mixed\$$TEST_MODULE"])
        fun bothOverloads(
            @IntRangeSource(min = 1, max = 3) start: Int,
            @IntRangeSource(min = 1, max = 3) end: Int
        ) {
        }

        companion object {
            @JvmStatic
            internal fun valueClassAndPublic_filter(amount: Money): Boolean = amount.cents != 2L

            fun valueClassAndPublic_filter(other: Int): Boolean = true

            internal fun valueClassAndInternal_filter(amount: Money): Boolean = amount.cents != 2L

            internal fun valueClassAndInternal_filter(cents: Long): Boolean = true

            fun conventional_filter(start: Int): Boolean = start >= 2

            internal fun conventional_filter(start: Int, end: Int): Boolean = true

            fun conventionalWithBridge_filter(start: Int): Boolean = start >= 2

            @JvmStatic
            internal fun conventionalWithBridge_filter(start: Int, end: Int): Boolean = true

            fun mixRule(start: Int): Boolean = start >= 2

            internal fun mixRule(start: Int, end: Int): Boolean = true

            internal fun mixRule(start: Int, end: Int, other: Int): Boolean = true

            fun mixed(start: Int): Boolean = start >= 2

            internal fun mixed(start: Int, end: Int): Boolean = start <= end
        }
    }

    open class ReplacedFilterBase {
        companion object {
            @JvmStatic
            fun replaced_filter_a(start: Int): Boolean = start >= 2
        }
    }

    class ReplacingFixture : ReplacedFilterBase() {
        @GeneratedParametersTest(name = "{arguments}")
        fun replaced(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        companion object {
            internal fun replaced_filter_a(start: Int): Boolean = start <= 2
        }
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

        @GeneratedParametersTest(name = "{arguments}", filters = ["compiledRule\$$TEST_MODULE"])
        fun compiledName(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["bothNames_filter_small\$$TEST_MODULE"])
        fun bothNames(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["bothNamesRule", "bothNamesRule\$$TEST_MODULE"])
        fun explicitBothNames(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        companion object {
            internal fun compiledRule(start: Int): Boolean = start >= 2

            internal fun bothNamesRule(start: Int): Boolean = start >= 2

            @JvmStatic
            internal fun bothNames_filter_small(start: Int): Boolean {
                compiledNameCalls.incrementAndGet()
                return start <= 2
            }

            @JvmStatic
            internal fun ordered_filter(start: Int, end: Int): Boolean = start <= end

            internal fun companionOnly_filter(start: Int, end: Int): Boolean = start <= end

            internal fun internalRule(start: Int, end: Int): Boolean = start <= end

            @JvmStatic
            internal fun rejectsAll_filter(start: Int): Boolean = false
        }
    }

    class OuterFixture : BaseFixture() {

        /** Only starts with `ordered`, so it doesn't claim `ordered_filter` from [Inner.ordered]. */
        @GeneratedParametersTest
        fun orderedPairs(@IntRangeSource(min = 1, max = 2) start: Int) {
        }

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

            @GeneratedParametersTest(filters = ["nope"])
            fun missing(@IntRangeSource(min = 1, max = 2) start: Int) {
            }

            @Nested
            inner class Deeper {
                @GeneratedParametersTest(name = "{arguments}")
                fun deeper(
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

            fun deeper_filter(start: Int, end: Int): Boolean = start <= end

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

        @GeneratedParametersTest(name = "{arguments}", filters = ["divides"])
        fun guarded(@IntRangeSource(min = 0, max = 2) d: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["alpha"])
        fun zeta(@IntRangeSource(min = 1, max = 3) start: Int) {
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

            @JvmStatic
            fun guarded_filter(d: Int): Boolean = d != 0

            @JvmStatic
            fun divides(d: Int): Boolean = 10 / d > 0

            @JvmStatic
            fun zeta_filter(start: Int): Boolean = false

            @JvmStatic
            fun alpha(start: Int): Boolean = true
        }
    }

    class ThrowingFixture {

        @GeneratedParametersTest(name = "{arguments}", filters = ["throwsAtTwo"])
        fun cartesian(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 2) end: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["throwsAtTwo"], strategy = GenerationStrategy.PAIRWISE)
        fun pairwise(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 2) end: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["throwsOnNull"])
        fun onNull(@IntSource([1]) start: Int, @StringSource(["a"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["assertsSmall"])
        fun asserting(@IntRangeSource(min = 1, max = 3) start: Int) {
        }

        companion object {
            @JvmStatic
            fun throwsAtTwo(start: Int): Boolean {
                check(start != 2) { "boom at start=$start" }
                return true
            }

            @JvmStatic
            fun throwsOnNull(label: String?): Boolean = checkNotNull(label) { "no label" }.isNotEmpty()

            @JvmStatic
            fun assertsSmall(start: Int): Boolean {
                if (start == 3) throw AssertionError("start is 3")
                return true
            }

            @JvmStatic
            fun outOfMemory(start: Int): Boolean = throw OutOfMemoryError("simulated")
        }
    }

    class NullFixture {

        @GeneratedParametersTest(name = "{arguments}")
        fun nullIntoNonNull(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = GenerationStrategy.PAIRWISE)
        fun nullIntoNonNullPairwise(@IntSource([1, 2]) n: Int, @StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["notB"])
        fun nullableType(@StringSource(["a", "b"]) label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startsWithA"])
        fun guarded(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startsWithA"], strategy = GenerationStrategy.PAIRWISE)
        fun guardedPairwise(@IntSource([1, 2]) n: Int, @StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["labelPresent", "startsWithA"])
        fun guardFirst(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startsWithA", "labelPresent"])
        fun guardSecond(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startsWithA"])
        fun guardedElsewhere(@IntSource([1, 2]) n: Int, @StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["bothNonNull"])
        fun oneOfTwoGuarded(
            @StringSource(["a"]) @WithNull first: String?,
            @StringSource(["b"]) @WithNull second: String?
        ) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["startsWithA"])
        fun leakyGuard(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["nullableNotB"])
        fun nullable(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["genericNotB"])
        fun generic(@StringSource(["a", "b"]) @WithNull label: String?) {
        }

        companion object {
            @JvmStatic
            fun nullIntoNonNull_filter(label: String): Boolean = label != "b"

            @JvmStatic
            fun nullIntoNonNullPairwise_filter(label: String): Boolean = label != "b"

            @JvmStatic
            fun notB(label: String): Boolean = label != "b"

            @JvmStatic
            fun startsWithA(label: String): Boolean = label.startsWith("a")

            @JvmStatic
            fun guarded_filter(label: String?): Boolean = label != null

            @JvmStatic
            fun guardedPairwise_filter(label: String?): Boolean = label != null

            @JvmStatic
            fun labelPresent(label: String?): Boolean = label != null

            @JvmStatic
            fun guardedElsewhere_filter(n: Int): Boolean = n == 1

            @JvmStatic
            fun oneOfTwoGuarded_filter(first: String?): Boolean = first != null

            @JvmStatic
            fun bothNonNull(first: String, second: String): Boolean = first != second

            @JvmStatic
            fun leakyGuard_filter(label: String?): Boolean = label != "b"

            @JvmStatic
            fun nullableNotB(label: String?): Boolean = label != "b"

            @JvmStatic
            fun <T> genericNotB(label: T): Boolean = label != "b"
        }
    }

    class KotlinTestJavaFilterFixture : JavaNullableFilters() {

        @GeneratedParametersTest(name = "{arguments}", filters = ["javaNotB"])
        fun label(@StringSource(["a", "b"]) @WithNull label: String?) {
        }
    }

    class ValueClassNullFixture {

        @GeneratedParametersTest(name = "{arguments}", filters = ["nonNullName"])
        fun name(@StringSource(["a"]) @WithNull name: Name?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["nonNullNickname"])
        fun nickname(@StringSource(["a"]) @WithNull nickname: Nickname?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["takesNullableName"])
        fun nullableName(@StringSource(["a"]) @WithNull name: Name?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["takesNullableMoney"])
        fun nullableMoney(@LongSource([5]) @WithNull amount: Money?) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["takesNullableNickname"])
        fun nullableNickname(@StringSource(["a"]) @WithNull nickname: Nickname?) {
        }

        companion object {
            @JvmStatic
            fun nonNullName(name: Name): Boolean = true

            @JvmStatic
            fun nonNullNickname(nickname: Nickname): Boolean = true

            @JvmStatic
            fun takesNullableName(name: Name?): Boolean = name == null

            @JvmStatic
            fun takesNullableMoney(amount: Money?): Boolean = amount == null

            @JvmStatic
            fun takesNullableNickname(nickname: Nickname?): Boolean = nickname == null
        }
    }
}

@JvmInline
value class Nickname(val value: String?)
