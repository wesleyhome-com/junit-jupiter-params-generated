package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.generator.GeneratedParametersTestExtension
import org.apiguardian.api.API
import org.apiguardian.api.API.Status.STABLE
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@API(status = STABLE, since = "3.0")
@TestTemplate
@ExtendWith(value = [GeneratedParametersTestExtension::class])
annotation class GeneratedParametersTest
