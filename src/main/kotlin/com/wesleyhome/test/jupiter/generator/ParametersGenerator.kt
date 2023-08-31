package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.InvalidParameterException
import com.wesleyhome.test.jupiter.provider.*
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
    LocalDateTimeRangeDataProvider
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
