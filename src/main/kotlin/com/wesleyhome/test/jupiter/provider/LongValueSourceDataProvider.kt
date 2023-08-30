package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LongRangeSource
import com.wesleyhome.test.jupiter.annotations.LongSource

object LongValueSourceDataProvider : AbstractParameterDataProvider<Long>() {

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
  }

  override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> {
    return findAnnotation(testParameter)!!.values.toList()
  }

  private fun findAnnotation(testParameter: TestParameter) =
    testParameter.annotations.firstOrNull { it is LongSource }.let {annotation ->
      if(annotation == null) {
        null
      } else {
        annotation as LongSource
      }
    }
}

object LongRangeDataProvider : AbstractParameterDataProvider<Long>() {

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
  }

  override fun createParameterOptionsData(testParameter: TestParameter): List<Long?> {
    val s = findAnnotation(testParameter)!!
    val range = s.min .. s.max step s.increment
    return range.toList()
  }

  private fun findAnnotation(testParameter: TestParameter) =
    testParameter.annotations.firstOrNull { it is LongRangeSource }.let {annotation ->
      if(annotation == null) {
        null
      } else {
        annotation as LongRangeSource
      }
    }
}
