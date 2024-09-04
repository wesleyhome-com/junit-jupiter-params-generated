package com.wesleyhome.test.jupiter.annotations.number

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest

class IntRangeSourceTest {

    @GeneratedParametersTest
    fun testIntRangeSource(@IntRangeSource(min = -1, max = 2) value: Int) {
    }
}
