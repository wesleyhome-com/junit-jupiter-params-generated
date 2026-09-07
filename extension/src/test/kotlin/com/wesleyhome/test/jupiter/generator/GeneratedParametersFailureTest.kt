package com.wesleyhome.test.jupiter.generator

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.hasMessage
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.datetime.LocalDateRangeSource
import com.wesleyhome.test.jupiter.annotations.ext.SourceProvider
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.testkit.executionFailure
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ParameterResolutionException
import org.junit.jupiter.params.ParameterizedTest
import org.junit.platform.commons.PreconditionViolationException
import java.time.LocalDate

/**
 * What a user actually sees when an annotation is misconfigured.
 *
 * These assertions were not expressible before: every fixture here fails on purpose, so running
 * them as ordinary tests would fail the build. The test task excludes `*Fixture` classes, and
 * [executionFailure] drives them through a real engine and hands back what came out.
 */
class GeneratedParametersFailureTest {

    @Test
    fun testSourceAnnotationOnAnUnsupportedParameterType() {
        assertThat(failure("sourceAnnotationOnUnsupportedType"))
            .isInstanceOf(InvalidParameterException::class)
            .hasMessage("Unable to find a suitable data provider for parameter [value] with type 'kotlin.String'")
    }

    @Test
    fun testSourceProviderPointingAtSomethingThatIsNotAProvider() {
        assertThat(failure("sourceProviderThatIsNotAProvider"))
            .isInstanceOf(InvalidParameterException::class)
            .hasMessage("Unable to find a suitable data provider for parameter [value] with type 'kotlin.Int'")
    }

    @Test
    fun testNumericRangeWithMinGreaterThanMax() {
        assertThat(failure("numericMinGreaterThanMax"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("Min value [10] cannot be greater than max value [1]")
    }

    @Test
    fun testNumericRangeWithAnIncrementOfZero() {
        assertThat(failure("numericIncrementOfZero"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("Increment must be greater than zero")
    }

    @Test
    fun testDateRangeWithMinGreaterThanMax() {
        assertThat(failure("dateMinGreaterThanMax"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("Min value [2024-06-01] cannot be greater than max value [2024-01-01]")
    }

    @Test
    fun testDateRangeWithAnUnparseableFormat() {
        assertThat(failure("unparseableDateFormat"))
            .isInstanceOf(IllegalArgumentException::class)
            .hasMessage("Invalid date format: [yyyy-MM-dd']")
    }

    /**
     * The other half of leaving non-generated parameters alone: when nothing else resolves one,
     * JUnit reports it, rather than this extension silently supplying null.
     */
    @Test
    fun testParameterNobodyResolvesIsReportedByJUnit() {
        val failure = failure("parameterNobodyResolves")
        assertThat(failure).isInstanceOf(ParameterResolutionException::class)
        assertThat(failure.message).isNotNull().all {
            contains("No ParameterResolver registered for parameter")
            contains("unresolvable")
        }
    }

    @Test
    fun testGeneratedAndParameterizedTogether() {
        assertThat(executionFailure(BothAnnotationsFixture::class.java, "generatedAndParameterized"))
            .isInstanceOf(PreconditionViolationException::class)
            .hasMessage("Test annotated with @GeneratedParametersTest cannot be annotated with @ParameterizedTest")
    }

    private fun failure(methodName: String) = executionFailure(Fixture::class.java, methodName)

    class Fixture {

        @GeneratedParametersTest
        fun sourceAnnotationOnUnsupportedType(@IntRangeSource(min = 1, max = 3) value: String) {
        }

        @GeneratedParametersTest
        fun sourceProviderThatIsNotAProvider(@NotAProviderSource value: Int) {
        }

        @GeneratedParametersTest
        fun numericMinGreaterThanMax(@IntRangeSource(min = 10, max = 1) value: Int) {
        }

        @GeneratedParametersTest
        fun numericIncrementOfZero(@IntRangeSource(min = 1, max = 10, increment = 0) value: Int) {
        }

        @GeneratedParametersTest
        fun dateMinGreaterThanMax(
            @LocalDateRangeSource(min = "2024-06-01", max = "2024-01-01") value: LocalDate
        ) {
        }

        @GeneratedParametersTest
        fun unparseableDateFormat(
            @LocalDateRangeSource(
                min = "2024-01-01",
                max = "2024-02-01",
                dateFormat = "yyyy-MM-dd'"
            ) value: LocalDate
        ) {
        }

        @GeneratedParametersTest
        fun parameterNobodyResolves(
            @IntRangeSource(min = 1, max = 2) value: Int,
            unresolvable: Unresolvable
        ) {
        }
    }

    class BothAnnotationsFixture {

        @GeneratedParametersTest
        @ParameterizedTest
        fun generatedAndParameterized(value: Int) {
        }
    }
}

/** A type no provider claims and no resolver supplies. */
class Unresolvable

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@SourceProvider(Unresolvable::class)
annotation class NotAProviderSource
