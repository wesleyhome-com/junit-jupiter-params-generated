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
    val range: IntProgression = s.min .. s.max step s.increment
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
