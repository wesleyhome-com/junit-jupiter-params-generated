package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.DoubleRangeSource

class DoubleRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Double, DoubleRangeSource>() {

    override fun convert(value: Number): Double = value.toDouble()
}
