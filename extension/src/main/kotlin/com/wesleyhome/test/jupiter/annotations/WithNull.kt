package com.wesleyhome.test.jupiter.annotations

import org.apiguardian.api.API
import org.apiguardian.api.API.Status.EXPERIMENTAL

/**
 * Adds `null` to the values generated for a parameter.
 *
 * Kotlin parameters declared nullable also get a null case today, inferred from the type. That
 * inference is Kotlin-only - a Java `Integer` is nullable and never received one - and it is
 * invisible at the use site, so a reader cannot tell from the test that a null is being generated.
 * Prefer this annotation, which reads the same in both languages and says so where it applies.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@API(status = EXPERIMENTAL, since = "4.1")
annotation class WithNull
