package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.StringSource
import com.wesleyhome.test.jupiter.annotations.datetime.InstantRangeSource
import com.wesleyhome.test.jupiter.provider.TestParameter
import com.wesleyhome.test.jupiter.step
import org.junit.jupiter.api.Disabled
import java.time.Instant

class InstantRangeSourceAbstractParameterDataProviderTest :
    AnnotatedDateTimeRangeDataProviderTest<InstantRangeSourceDataProvider, Instant, InstantRangeSource>() {

    override val defaultIncrementString: String = "PT1h"

    @GeneratedParametersTest
    @Disabled
    fun testCreateParameterOptionsData(
        @StringSource(["", "Unparseable", "2024-06-01T12:00:00Z"])
        min: String,
        @StringSource(["", "Unparseable", "2024-06-02T12:00:00Z"])
        max: String,
        @StringSource(["", "Unparseable", "PT2h", "PT6h"])
        increment: String,
        ascending: Boolean
    ) {
        createAndAssertTestParameter(min, max, increment, ascending)
    }

    override fun testParameter(
        min: String,
        max: String,
        increment: String?,
        ascending: Boolean?,
        dateFormat: String?
    ): TestParameter {
        return createAnnotatedTestParameter(min, max, increment, ascending)
    }

    override fun createTrueProvidesForTestParameter(): TestParameter =
        testParameter("2024-06-01T12:00:00Z", "2024-06-02T12:00:00Z", "PT1H", true, null)


    override fun convert(valueString: String, format: String): Instant {
        return valueString.toInstant()
    }

    override fun expectedList(min: Instant, max: Instant, increment: String): List<Instant> {
        return (min..max step increment).toList()
    }
}
