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
    if (s.increment <= 0) {
      throw IllegalArgumentException("increment must be greater than 0")
    }
    if(s.min >= s.max) {
        throw IllegalArgumentException("min must be less than or equal to max")
    }
    val range = if(s.ascending) {
        s.min .. s.max step s.increment
    } else {
        s.max downTo s.min step s.increment
    }
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
