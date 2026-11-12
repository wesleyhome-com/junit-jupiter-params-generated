package examples

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy.PAIRWISE
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.ValuesFrom
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.annotations.number.LongSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.LocalDate
import java.util.UUID

/**
 * Filters, unlike the value sources, cannot be shown as local functions inside an `example()` method:
 * they run before the test instance exists and so must be static or live in a companion object,
 * neither of which is declarable inside a function body. These samples are therefore whole classes.
 */
class Filtering {

    /**
     * A method named after its test filters that test without being declared anywhere.
     *
     * Ten of the sixteen combinations have `minPrice` above `maxPrice` and are never generated,
     * rather than being generated and skipped inside the test.
     */
    @GeneratedParametersTest(name = "{arguments}")
    fun priceRange(
        @IntRangeSource(min = 1, max = 4) minPrice: Int,
        @IntRangeSource(min = 1, max = 4) maxPrice: Int
    ) {
        assertThat(minPrice).isBetween(1, maxPrice)
    }

    /**
     * Several rules for one test, each naming only the parameters it is about. A combination runs
     * only if every rule accepts it, so rules stay small instead of one predicate knowing everything.
     */
    @GeneratedParametersTest(name = "{arguments}")
    fun discount(
        @IntRangeSource(min = 1, max = 4) quantity: Int,
        @IntRangeSource(min = 0, max = 50, increment = 25) percentOff: Int,
        @StringSource(["standard", "member"]) tier: String
    ) {
        assertThat(quantity).isBetween(1, 4)
    }

    companion object {

        @JvmStatic
        fun priceRange_filter(minPrice: Int, maxPrice: Int): Boolean = minPrice <= maxPrice

        @JvmStatic
        fun discount_filter_bulkOnly(quantity: Int, percentOff: Int): Boolean =
            percentOff == 0 || quantity > 1

        @JvmStatic
        fun discount_filter_membersOnly(percentOff: Int, tier: String): Boolean =
            percentOff <= 25 || tier == "member"
    }
}

/**
 * Pairwise runs enough combinations that every pair of parameters appears with every pair of their
 * values. It gives up combinations needing three or more specific values at once.
 */
class Pairwise {

    /**
     * Eighteen combinations reduced to nine, still covering every browser with every operating
     * system, every browser with every locale, and every operating system with every locale.
     */
    @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
    fun rendersEverywhere(
        @StringSource(["chrome", "firefox", "safari"]) browser: String,
        @StringSource(["windows", "mac", "linux"]) os: String,
        @StringSource(["en", "fr"]) locale: String
    ) {
        assertThat(browser).isNotNull()
    }

    /**
     * Pairwise and a filter together. Safari only exists on mac, so `safari + windows` is not a pair
     * any combination can contain and is not demanded; `safari + mac` is reachable and is still
     * guaranteed to appear.
     *
     * The pairs to cover come from the combinations the filter accepts. Building a covering array
     * first and filtering it afterwards would drop whichever rows happened to carry a reachable pair
     * and quietly stop honouring the guarantee.
     */
    @GeneratedParametersTest(name = "{arguments}", strategy = PAIRWISE)
    fun rendersOnSupportedPlatforms(
        @StringSource(["chrome", "firefox", "safari"]) browser: String,
        @StringSource(["windows", "mac", "linux"]) os: String,
        @StringSource(["en", "fr"]) locale: String
    ) {
        assertThat(browser).isNotNull()
    }

    companion object {

        @JvmStatic
        fun rendersOnSupportedPlatforms_filter(browser: String, os: String): Boolean =
            browser != "safari" || os == "mac"
    }
}

/** `@WithNull` adds a null case, and reads the same from Kotlin and from Java. */
class NullValues {

    fun example() {

        /** Runs with "ada", "grace" and null. */
        @GeneratedParametersTest(name = "{arguments}")
        fun handlesMissingName(@StringSource(["ada", "grace"]) @WithNull name: String?) {
            assertThat(name?.length ?: 0).isBetween(0, 10)
        }
    }
}

/**
 * A value class takes its values from any source for the type it wraps, built through its
 * constructor so its `init` checks run. Value classes cannot be declared inside a function body, so
 * this sample is a whole class too.
 */
class ValueClasses {

    @JvmInline
    value class Money(val cents: Long) {
        init {
            require(cents >= 0) { "cents must be >= 0" }
        }
    }

    @JvmInline
    value class HouseholdId(val value: UUID)

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.RUNTIME)
    @SourceProvider(HouseholdIdSourceDataProvider::class)
    annotation class HouseholdIdSource

    /** No built-in source covers UUID, so this provider produces the value class directly. */
    class HouseholdIdSourceDataProvider : AbstractAnnotatedParameterDataProvider<HouseholdId, HouseholdIdSource>() {
        override fun createParameterOptionsData(testParameter: TestParameter): List<HouseholdId> =
            listOf(HouseholdId(UUID.fromString("00000000-0000-0000-0000-000000000001")))
    }

    /** Runs with Money(cents=0), Money(cents=1) and Money(cents=250), each with quantity 1 and 2. */
    @GeneratedParametersTest(name = "{arguments}")
    fun formatsAmounts(@LongSource([0, 1, 250]) amount: Money, @IntSource([1, 2]) quantity: Int) {
        assertThat(amount.cents * quantity).isGreaterThanOrEqualTo(0L)
    }

    /** Runs with Money(cents=5) and null. */
    @GeneratedParametersTest(name = "{arguments}")
    fun handlesMissingAmount(@LongSource([5]) @WithNull amount: Money?) {
        assertThat(amount?.cents ?: 0L).isGreaterThanOrEqualTo(0L)
    }

    @GeneratedParametersTest(name = "{arguments}")
    fun loadsHousehold(@HouseholdIdSource id: HouseholdId) {
        assertThat(id.value).isNotNull()
    }

    /** Free orders are never generated: the filter declares the value class, not the Long it wraps. */
    @GeneratedParametersTest(name = "{arguments}")
    fun chargesCustomer(@LongSource([0, 1, 250]) amount: Money) {
        assertThat(amount.cents).isGreaterThanOrEqualTo(1L)
    }

    companion object {

        @JvmStatic
        fun chargesCustomer_filter(amount: Money): Boolean = amount.cents > 0
    }
}

/**
 * `@ValuesFrom` takes a parameter's values from a function, for types no annotation can spell out.
 * The function has to be callable before the test instance exists, so this sample is a whole class.
 */
class ValuesFrom {

    /** Runs with every ordered pair of the three ranges: nine invocations. */
    @GeneratedParametersTest(name = "{arguments}")
    fun overlapsIsSymmetric(@ValuesFrom("ranges") a: DateRange, @ValuesFrom("ranges") b: DateRange) {
        assertThat(a.overlaps(b)).isEqualTo(b.overlaps(a))
    }

    /** A function on an object, named with its class, can be shared between test classes. */
    @GeneratedParametersTest(name = "{arguments}")
    fun overlapsItself(@ValuesFrom("examples.SharedRanges#ranges") range: DateRange) {
        assertThat(range.overlaps(range)).isTrue()
    }

    companion object {

        @JvmStatic
        fun ranges(): List<DateRange> = listOf(
            DateRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
            DateRange(LocalDate.of(2024, 1, 15), LocalDate.of(2024, 2, 15)),
            DateRange(LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31))
        )
    }
}

data class DateRange(val start: LocalDate, val endInclusive: LocalDate) {
    fun overlaps(other: DateRange): Boolean = start <= other.endInclusive && other.start <= endInclusive
}

object SharedRanges {
    fun ranges(): List<DateRange> = listOf(DateRange(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30)))
}
