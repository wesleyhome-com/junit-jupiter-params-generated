package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.FloatRangeSource
import com.wesleyhome.test.jupiter.annotations.FloatSource
import java.util.concurrent.atomic.AtomicReference

object FloatValueSourceDataProvider : AbstractParameterDataProvider<Float>() {

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
  }

  override fun createParameterOptionsData(testParameter: TestParameter): List<Float?> {
    return findAnnotation(testParameter)!!.values.toList()
  }

  private fun findAnnotation(testParameter: TestParameter) =
    testParameter.annotations.firstOrNull { it is FloatSource }.let {annotation ->
      if(annotation == null) {
        null
      } else {
        annotation as FloatSource
      }
    }
}

object FloatRangeDataProvider : AbstractParameterDataProvider<Float>() {

  override fun providesDataFor(testParameter: TestParameter): Boolean {
    return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
  }

  override fun createParameterOptionsData(testParameter: TestParameter): List<Float?> {
    val s = findAnnotation(testParameter)!!
    if(s.increment <= 0) {
      throw IllegalArgumentException("increment must be greater than 0")
    }
    if(s.min >= s.max) {
      throw IllegalArgumentException("min must be less than or equal to max")
    }
    val range = (s.min .. s.max step s.increment).toList()
    return if(s.ascending) {
      range
    } else {
      range.reversed()
    }
  }

  private fun findAnnotation(testParameter: TestParameter) =
    testParameter.annotations.firstOrNull { it is FloatRangeSource }.let {annotation ->
      if(annotation == null) {
        null
      } else {
        annotation as FloatRangeSource
      }
    }
}

class FloatProgression(
  private val min: Float,
  private val max: Float,
  private val step: Float
) : Iterable<Float> {

  private val range: ClosedFloatingPointRange<Float> = min .. max
  private val current = AtomicReference(min)

  override fun iterator(): Iterator<Float> {
    return object : Iterator<Float> {
      override fun hasNext(): Boolean = range.contains(current.get())

      override fun next(): Float {
        return current.getAndUpdate { it + step }
      }
    }
  }
}

infix fun ClosedFloatingPointRange<Float>.step(step: Float) : FloatProgression {
  return FloatProgression(this.start, this.endInclusive, step)
}
