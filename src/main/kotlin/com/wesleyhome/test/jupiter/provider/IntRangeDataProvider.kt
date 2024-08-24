package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.IntRangeSource

class IntRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Int, IntRangeSource>() {

    override fun convert(value: Number): Int = value.toInt()
}
