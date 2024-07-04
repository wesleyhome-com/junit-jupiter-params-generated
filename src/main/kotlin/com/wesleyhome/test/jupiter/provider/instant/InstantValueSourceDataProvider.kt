package com.wesleyhome.test.jupiter.provider.instant

import com.wesleyhome.test.jupiter.annotations.InstantSource
import com.wesleyhome.test.jupiter.provider.AbstractAnnotatedParameterDataProvider
import com.wesleyhome.test.jupiter.provider.TestParameter
import java.time.Instant
import kotlin.reflect.KClass

object InstantValueSourceDataProvider : AbstractAnnotatedParameterDataProvider<Instant, InstantSource>() {

    override val annotation: KClass<InstantSource> = InstantSource::class

    override fun providesDataFor(testParameter: TestParameter): Boolean {
        return super.providesDataFor(testParameter) && findAnnotation(testParameter) != null
    }

    override fun createParameterOptionsData(testParameter: TestParameter): List<Instant?> {
        val instantSource = findAnnotation(testParameter)!!
        return instantSource.values.map { Instant.parse(it) }
    }
}
