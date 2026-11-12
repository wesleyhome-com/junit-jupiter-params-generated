package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasMessage
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.messageContains
import com.wesleyhome.test.jupiter.InvalidFilterException
import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.annotations.number.LongSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.testkit.executeFixture
import com.wesleyhome.test.jupiter.testkit.executionFailure
import com.wesleyhome.test.jupiter.testkit.invocationCounts
import com.wesleyhome.test.jupiter.testkit.invocationNames
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ParameterResolutionException
import java.util.UUID

/**
 * A value-class parameter compiles to a method taking the underlying type - `amount: Money` is a
 * `long` - so the generated box has to be unwrapped before JUnit will accept it.
 */
class ValueClassTest {

    @Test
    fun testLongSourceGeneratesAValueClassOverLong() {
        assertThat(invocationNames(Fixture::class.java, "overLong"))
            .isEqualTo(listOf("Money(cents=0)", "Money(cents=1)"))
    }

    @Test
    fun testIntSourceGeneratesAValueClassOverInt() {
        assertThat(invocationNames(Fixture::class.java, "overInt")).isEqualTo(listOf("Count(value=3)", "Count(value=4)"))
    }

    @Test
    fun testStringSourceGeneratesAValueClassOverString() {
        assertThat(invocationNames(Fixture::class.java, "overString")).isEqualTo(listOf("Name(value=a)", "Name(value=b)"))
    }

    @Test
    fun testCustomProviderOfAValueClassOverLong() {
        assertThat(invocationNames(Fixture::class.java, "customMoney"))
            .isEqualTo(listOf("Money(cents=100)", "Money(cents=250)"))
    }

    @Test
    fun testCustomProviderOfAValueClassOverUuid() {
        assertThat(invocationNames(Fixture::class.java, "customHouseholdId")).isEqualTo(
            listOf("HouseholdId(value=$FIRST_HOUSEHOLD)", "HouseholdId(value=$SECOND_HOUSEHOLD)")
        )
    }

    /** One bad value must not stop the others from running, nor be skipped where nobody sees it. */
    @Test
    fun testValueRejectedByInitFailsOnlyItsOwnInvocation() {
        assertThat(invocationCounts(Fixture::class.java, "rejected")).isEqualTo(3L to 2L)
        assertThat(invocationNames(Fixture::class.java, "rejected"))
            .isEqualTo(listOf("Money(cents=0)", "Money(cents=1)"))
    }

    @Test
    fun testRejectedValueNamesTheParameterTheValueAndTheCause() {
        val failure = executionFailure(Fixture::class.java, "rejected")
        assertThat(failure).isInstanceOf(ParameterResolutionException::class)
            .hasMessage(
                "Generated value [-1] for parameter [amount] was rejected by Money: " +
                    "IllegalArgumentException: cents must be >= 0"
            )
        assertThat(failure.cause!!).isInstanceOf(IllegalArgumentException::class)
    }

    @Test
    fun testRejectedValueIsNamedInTheDisplayName() {
        val displayNames = executeFixture(Fixture::class.java, "rejected")
            .testEvents().failed().list().map { it.testDescriptor.displayName }
        assertThat(displayNames).isEqualTo(listOf("Money(-1) rejected"))
    }

    /** `Money?` over a primitive stays boxed in the signature; `Name?` over a reference is a `String`. */
    @Test
    fun testNullableValueClassOverAPrimitiveIsPassedBoxed() {
        assertThat(invocationNames(Fixture::class.java, "nullableOverLong")).isEqualTo(listOf("Money(cents=5)", "null"))
    }

    @Test
    fun testNullableValueClassOverAReferenceIsPassedUnboxed() {
        assertThat(invocationNames(Fixture::class.java, "nullableOverString")).isEqualTo(listOf("Name(value=a)", "null"))
    }

    @Test
    fun testValueClassMixedWithOrdinaryParameters() {
        assertThat(invocationNames(Fixture::class.java, "mixed")).isEqualTo(
            listOf(
                "1, Money(cents=0), true", "1, Money(cents=0), false",
                "1, Money(cents=1), true", "1, Money(cents=1), false",
                "2, Money(cents=0), true", "2, Money(cents=0), false",
                "2, Money(cents=1), true", "2, Money(cents=1), false"
            )
        )
    }

    /** The test's JVM name is mangled to `byConvention-<hash>`, which must not hide its filter. */
    @Test
    fun testConventionalFilterAppliesToATestTakingAValueClass() {
        assertThat(invocationNames(Fixture::class.java, "byConvention"))
            .isEqualTo(listOf("Money(cents=0), 1", "Money(cents=0), 2", "Money(cents=1), 2"))
    }

    @Test
    fun testFilterDeclaringAValueClassReceivesIt() {
        assertThat(invocationNames(Fixture::class.java, "explicitFilter")).isEqualTo(listOf("Money(cents=2)"))
    }

    @Test
    fun testFilterCannotHideARejectedValue() {
        assertThat(invocationCounts(Fixture::class.java, "rejectedThroughFilter")).isEqualTo(1L to 0L)
        assertThat(executionFailure(Fixture::class.java, "rejectedThroughFilter"))
            .isInstanceOf(ParameterResolutionException::class)
    }

    /** Both compile to `long`, so only the Kotlin declaration shows the filter asked for the wrong type. */
    @Test
    fun testFilterDeclaringTheUnderlyingTypeIsRejected() {
        assertThat(executionFailure(Fixture::class.java, "underlyingTypeFilter"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage(
                "Filter [underlyingTypeFilter_filter] declares [amount] as long, but the generated parameter is Money"
            )
    }

    @Test
    fun testPairwiseCoversValueClassParameters() {
        assertThat(invocationCounts(Fixture::class.java, "pairwise")).isEqualTo(4L to 4L)
    }

    @Test
    fun testWithNullOnANonNullValueClassOverAPrimitiveIsRejected() {
        assertThat(executionFailure(Fixture::class.java, "withNullOnNonNull"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("@WithNull cannot be applied to [amount]: long cannot hold null")
    }

    @Test
    fun testSourceForAnotherUnderlyingTypeIsStillMisapplied() {
        assertThat(executionFailure(Fixture::class.java, "wrongUnderlying"))
            .isInstanceOf(InvalidParameterException::class)
            .hasMessage(
                "Unable to find a suitable data provider for parameter [name] with type '${Name::class.qualifiedName}'"
            )
    }

    /** No annotation means not ours, even though Boolean values would be generated for the underlying type. */
    @Test
    fun testUnannotatedValueClassIsLeftToOtherResolvers() {
        assertThat(executionFailure(Fixture::class.java, "unannotated"))
            .isInstanceOf(ParameterResolutionException::class)
            .messageContains("No ParameterResolver registered")
    }

    /** A type parameter has no class to look a source up by, which must not surface as a ClassCastException. */
    @Test
    fun testGenericValueClassIsReportedAsMisapplied() {
        assertThat(executionFailure(Fixture::class.java, "generic"))
            .isInstanceOf(InvalidParameterException::class)
            .hasMessage(
                "Unable to find a suitable data provider for parameter [box] with type '${Box::class.qualifiedName}'"
            )
    }

    /** The second null is the one every nullable parameter gets, added after the source's own. */
    @Test
    fun testNullFromTheUnderlyingSourceIsNotWrapped() {
        assertThat(invocationNames(Fixture::class.java, "nullFromUnderlyingSource"))
            .isEqualTo(listOf("Money(cents=7)", "null", "null"))
    }

    @Test
    fun testConventionalFilterNameMustMatchExactlyOrBySuffix() {
        assertThat(invocationNames(Fixture::class.java, "lookalike")).isEqualTo(listOf("Money(cents=1)"))
    }

    @Test
    fun testFilterNameThatIsOnlyAPrefixOfAFunctionDoesNotMatchIt() {
        assertThat(executionFailure(Fixture::class.java, "prefixOnly"))
            .isInstanceOf(InvalidFilterException::class)
            .hasMessage("No filter named [onlyTw] on ${Fixture::class.java.name}")
    }

    class Fixture {

        @GeneratedParametersTest
        fun generic(@LongSource([1]) box: Box<Long>) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun nullFromUnderlyingSource(@SevenOrNullSource amount: Money?) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun lookalike(@LongSource([1]) amount: Money) {
        }

        @GeneratedParametersTest(filters = ["onlyTw"])
        fun prefixOnly(@LongSource([1]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun overLong(@LongSource([0, 1]) amount: Money) {
            check(amount.cents in 0L..1L)
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun overInt(@IntSource([3, 4]) count: Count) {
            check(count.value in 3..4)
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun overString(@StringSource(["a", "b"]) name: Name) {
            check(name.value in setOf("a", "b"))
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun customMoney(@MoneySource amount: Money) {
            check(amount.cents in setOf(100L, 250L))
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun customHouseholdId(@HouseholdIdSource id: HouseholdId) {
            check(id.value in setOf(FIRST_HOUSEHOLD, SECOND_HOUSEHOLD))
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun rejected(@LongSource([0, 1, -1]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun nullableOverLong(@LongSource([5]) @WithNull amount: Money?) {
            check(amount == null || amount.cents == 5L)
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun nullableOverString(@StringSource(["a"]) @WithNull name: Name?) {
            check(name == null || name.value == "a")
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun mixed(@IntSource([1, 2]) n: Int, @LongSource([0, 1]) amount: Money, flag: Boolean) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun byConvention(@LongSource([0, 1, 2]) amount: Money, @IntSource([1, 2]) n: Int) {
        }

        @GeneratedParametersTest(name = "{arguments}", filters = ["onlyTwo"])
        fun explicitFilter(@LongSource([1, 2, 3]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun rejectedThroughFilter(@LongSource([1, -1]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}")
        fun underlyingTypeFilter(@LongSource([1]) amount: Money) {
        }

        @GeneratedParametersTest(name = "{arguments}", strategy = GenerationStrategy.PAIRWISE)
        fun pairwise(@LongSource([0, 1]) amount: Money, @IntSource([1, 2]) count: Count, flag: Boolean) {
        }

        @GeneratedParametersTest
        fun withNullOnNonNull(@LongSource([1]) @WithNull amount: Money) {
        }

        @GeneratedParametersTest
        fun wrongUnderlying(@LongSource([1]) name: Name) {
        }

        @GeneratedParametersTest
        fun unannotated(@IntSource([1]) n: Int, flag: Flag) {
        }

        companion object {
            @JvmStatic
            fun byConvention_filter(amount: Money, n: Int): Boolean = amount.cents < n

            @JvmStatic
            fun onlyTwo(amount: Money): Boolean = amount.cents == 2L

            @JvmStatic
            fun rejectedThroughFilter_filter(amount: Money): Boolean = amount.cents > 5

            @JvmStatic
            fun underlyingTypeFilter_filter(amount: Long): Boolean = amount > 0

            /** Shares the `lookalike_filter` prefix without being a filter for it; applied, it would reject everything. */
            @JvmStatic
            fun lookalike_filtering(amount: Money): Boolean = false
        }
    }
}

private val FIRST_HOUSEHOLD: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
private val SECOND_HOUSEHOLD: UUID = UUID.fromString("00000000-0000-0000-0000-000000000002")

@JvmInline
value class Money(val cents: Long) {
    init {
        require(cents >= 0) { "cents must be >= 0" }
    }
}

@JvmInline
value class Count(val value: Int)

@JvmInline
value class Name(val value: String)

@JvmInline
value class HouseholdId(val value: UUID)

@JvmInline
value class Flag(val value: Boolean)

@JvmInline
value class Box<T>(val value: T)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(SevenOrNullSourceDataProvider::class)
annotation class SevenOrNullSource

class SevenOrNullSourceDataProvider : AbstractAnnotatedParameterDataProvider<Long, SevenOrNullSource>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> = listOf(7L, null)
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(MoneySourceDataProvider::class)
annotation class MoneySource

class MoneySourceDataProvider : AbstractAnnotatedParameterDataProvider<Money, MoneySource>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<Money> = listOf(Money(100), Money(250))
}

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(HouseholdIdSourceDataProvider::class)
annotation class HouseholdIdSource

class HouseholdIdSourceDataProvider : AbstractAnnotatedParameterDataProvider<HouseholdId, HouseholdIdSource>() {
    override fun createParameterOptionsData(testParameter: TestParameter): List<HouseholdId> =
        listOf(HouseholdId(FIRST_HOUSEHOLD), HouseholdId(SECOND_HOUSEHOLD))
}
