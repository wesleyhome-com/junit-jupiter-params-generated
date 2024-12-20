package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource

internal class IntRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Int, IntRangeSource>() {

    override fun convert(value: Number): Int = value.toInt()
}
