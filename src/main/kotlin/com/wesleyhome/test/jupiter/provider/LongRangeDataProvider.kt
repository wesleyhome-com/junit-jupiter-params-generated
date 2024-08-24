package com.wesleyhome.test.jupiter.provider

import com.wesleyhome.test.jupiter.annotations.LongRangeSource

class LongRangeDataProvider : AbstractAnnotatedNumberRangeDataProvider<Long, LongRangeSource>() {

    override fun convert(value: Number): Long = value.toLong()
}
