package examples

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isNotNull
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.GenerationStrategy.PAIRWISE
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource

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
