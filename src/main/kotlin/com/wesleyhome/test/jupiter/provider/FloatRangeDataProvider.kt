package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.FloatRangeSource

class FloatRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Float, FloatRangeSource>() {

    override fun convert(value: Number): Float = value.toFloat()
}
