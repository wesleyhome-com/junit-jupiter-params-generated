package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.generator.GeneratedParametersTestExtension
import org.apiguardian.api.API
import org.apiguardian.api.API.Status.STABLE
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Marks a test whose parameters are generated, running it once per combination of generated values.
 *
 * A method named `<test>_filter` filters that test without being listed anywhere, so invalid
 * combinations are never generated rather than generated and skipped. Use `<test>_filter_<name>` for
 * several rules, and [filters] to share a rule between tests. A filter takes only the parameters it
 * is about, matched by name, and must be static or in a companion object because filtering happens
 * before the test instance exists.
 *
 * `strategy = PAIRWISE` covers every pair of parameters with every pair of their values instead of
 * the whole product, and takes the pairs to cover from the combinations the filters accept. See
 * [GenerationStrategy].
 *
 * Worked versions of both, compiled and checked by the annotation processor, are `examples.Filtering`
 * and `examples.Pairwise` in the `examples` module; the README shows them in full.
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
    val name: String = DEFAULT_DISPLAY_NAME,
    /**
     * Names of methods that decide which generated combinations run. Each is matched by parameter
     * name to the generated parameters it declares, so a rule names only what it is about, and a
     * combination runs only if every filter accepts it.
     *
     * Filtering happens before the test instance exists, so a filter must be static or declared in
     * a companion object - the same constraint `@MethodSource` has.
     */
    val filters: Array<String> = [],
    /**
     * How combinations are chosen. Opt in per test method: whether pairwise coverage is enough
     * depends on what the test asserts, and only its author knows.
     */
    val strategy: GenerationStrategy = GenerationStrategy.CARTESIAN
)

/**
 * The default [GeneratedParametersTest.name], matching the default of `@ParameterizedTest` so that
 * IDEs and test reports read generated invocations the same way they read parameterized ones.
 */
const val DEFAULT_DISPLAY_NAME: String = "[{index}] {argumentsWithNames}"
