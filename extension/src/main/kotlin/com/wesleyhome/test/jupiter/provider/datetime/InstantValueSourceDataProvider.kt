package com.wesleyhome.test.jupiter.provider.datetime

import com.wesleyhome.test.jupiter.annotations.datetime.InstantSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Instant

internal class InstantValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Instant, InstantSource>() {

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val instantSource = findAnnotation(testParameter)!!
        return instantSource.values.map { Instant.parse(it) }
    }
}
