package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.IntSource
import java.util.concurrent.atomic.AtomicReference

object IntValueSourceDataProvider : AbstractParameterDataProvider<Int>() {

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
  }

  override fun createParameterOptionsData(testParameter: TestParameter): List<Int?> {
    return findAnnotation(testParameter)!!.values.toList()
  }

  private fun findAnnotation(testParameter: TestParameter) =
    testParameter.annotations.firstOrNull { it is IntSource }.let {annotation ->
      if(annotation == null) {
        null
      } else {
        annotation as IntSource
      }
    }
}

object IntRangeDataProvider : AbstractParameterDataProvider<Int>() {

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
  }

  override fun createParameterOptionsData(testParameter: TestParameter): List<Int?> {
    val s = findAnnotation(testParameter)!!
    if(s.increment <= 0) {
      throw IllegalArgumentException("increment must be greater than 0")
    }
    if(s.min >= s.max) {
      throw IllegalArgumentException("min must be less than or equal to max")
    }
    val range: IntProgression = if(s.ascending) {
        s.min .. s.max step s.increment
    } else {
        s.max downTo s.min step s.increment
    }
    return range.toList()
  }

  private fun findAnnotation(testParameter: TestParameter) =
    testParameter.annotations.firstOrNull { it is IntRangeSource }.let {annotation ->
      if(annotation == null) {
        null
      } else {
        annotation as IntRangeSource
      }
    }
}
