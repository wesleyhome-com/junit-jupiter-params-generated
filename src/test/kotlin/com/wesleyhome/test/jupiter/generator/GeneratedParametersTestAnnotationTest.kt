package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import kotlin.reflect.KParameter

class GeneratedParametersTestAnnotationTest {

    @GeneratedParametersTest
    fun test(output: KParameter.Kind, value: Boolean) {
        // do nothing
        println("${output}-$value")
    }
}

