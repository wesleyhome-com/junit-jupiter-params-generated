package com.wesleyhome.test.jupiter.annotations

import org.apiguardian.api.API
import org.apiguardian.api.API.Status.STABLE

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@API(status = STABLE, since = "3.0")
annotation class DefaultProvider(val priority: Int = Int.MAX_VALUE)
