package com.wesleyhome.test.jupiter.provider.number

import com.wesleyhome.test.jupiter.annotations.number.LongRangeSource

internal class LongRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Long, LongRangeSource>() {

    override fun convert(value: Number): Long = value.toLong()
}
