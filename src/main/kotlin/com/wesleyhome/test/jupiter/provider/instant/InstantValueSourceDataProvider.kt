package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.InstantSource
import com.wesleyhome.test.jupiter.provider.AbstractParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Instant

object InstantValueSourceDataProvider : AbstractParameterDataProvider<Instant>() {

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val instantSource = findAnnotation(testParameter)!!
        return instantSource.values.map { Instant.parse(it) }
    }


    private fun findAnnotation(testParameter: TestParameter) =
        testParameter.annotations.firstOrNull { it is InstantSource }.let { annotation ->
            if (annotation == null) {
                null
            } else {
                annotation as InstantSource
            }
        }

}
