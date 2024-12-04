package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.DoubleRangeSource

internal class DoubleRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Double, DoubleRangeSource>() {

    override fun convert(value: Number): Double = value.toDouble()
}
