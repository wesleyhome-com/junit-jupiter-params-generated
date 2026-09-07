package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.generator.GeneratedParametersTestExtension
import org.apiguardian.api.API
import org.apiguardian.api.API.Status.STABLE
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Marks a test whose parameters are generated, running it once per combination of generated values.
 *
 * @property name the display name given to each invocation. Supports the same placeholders as
 * `@ParameterizedTest`: `{index}` for the 1-based invocation number, `{arguments}` for the generated
 * values, `{argumentsWithNames}` for `name=value` pairs, `{displayName}` for the test method's own
 * display name, and `{0}`, `{1}` and so on for a single generated value by position. Only generated
 * parameters are listed; parameters resolved by other extensions are not.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@API(status = STABLE, since = "3.0")
@TestTemplate
@ExtendWith(value = [GeneratedParametersTestExtension::class])
annotation class GeneratedParametersTest(
    val name: String = DEFAULT_DISPLAY_NAME
)

/**
 * The default [GeneratedParametersTest.name], matching the default of `@ParameterizedTest` so that
 * IDEs and test reports read generated invocations the same way they read parameterized ones.
 */
const val DEFAULT_DISPLAY_NAME: String = "[{index}] {argumentsWithNames}"
