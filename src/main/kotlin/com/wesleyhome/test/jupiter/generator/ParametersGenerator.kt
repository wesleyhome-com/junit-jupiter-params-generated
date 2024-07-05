package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.provider.BooleanParameterDataProvider
import com.wesleyhome.test.jupiter.provider.DoubleRangeDataProvider
import com.wesleyhome.test.jupiter.provider.DoubleValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.EnumParameterDataProvider
import com.wesleyhome.test.jupiter.provider.FloatRangeDataProvider
import com.wesleyhome.test.jupiter.provider.FloatValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.IntRangeDataProvider
import com.wesleyhome.test.jupiter.provider.IntValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.LocalDateRangeDataProvider
import com.wesleyhome.test.jupiter.provider.LocalDateTimeRangeDataProvider
import com.wesleyhome.test.jupiter.provider.LocalDateTimeValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.LocalDateValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.LocalTimeRangeDataProvider
import com.wesleyhome.test.jupiter.provider.LocalTimeValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.LongRangeDataProvider
import com.wesleyhome.test.jupiter.provider.LongValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.ParameterDataProvider
import com.wesleyhome.test.jupiter.provider.StringValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.TestModel
import com.wesleyhome.test.jupiter.provider.instant.InstantRangeSourceDataProvider
import com.wesleyhome.test.jupiter.provider.instant.InstantValueSourceDataProvider
import com.wesleyhome.test.jupiter.provider.instant.RandomInstanceSourceDataProvider
import org.junit.jupiter.params.provider.Arguments

class ParametersGenerator(
    testModel: TestModel
) {

    private val dataProviders: List<ParameterDataProvider<*>> = listOf(
        BooleanParameterDataProvider,
        EnumParameterDataProvider,
        IntValueSourceDataProvider,
        IntRangeDataProvider,
        LongValueSourceDataProvider,
        LongRangeDataProvider,
        DoubleValueSourceDataProvider,
        DoubleRangeDataProvider,
        FloatValueSourceDataProvider,
        FloatRangeDataProvider,
        LocalDateValueSourceDataProvider,
        LocalDateRangeDataProvider,
        LocalDateTimeValueSourceDataProvider,
        LocalDateTimeRangeDataProvider,
        StringValueSourceDataProvider,
        LocalTimeValueSourceDataProvider,
        LocalTimeRangeDataProvider,
        InstantValueSourceDataProvider,
        RandomInstanceSourceDataProvider,
        InstantRangeSourceDataProvider
    )
    private val options: List<List<Any?>> = testModel.testParameters.map {
        dataProviders.firstOrNull { dp ->
            dp.providesDataFor(it)
        }?.createParameterOptionsData(it)
            ?: throw InvalidParameterException(it.type)
    }

    fun arguments(): Iterable<Arguments> {
        return ArgumentParameters(options)
    }
}
