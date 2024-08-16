package com.wesleyhome.test.jupiter.annotations

import com.wesleyhome.test.jupiter.provider.ParametersSourceArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource

/**
 * `@ParametersSource` is an [ArgumentsSource] which provides access
 * which this annotation is declared.
 *
 *
 *
 * By default such methods must be `static` unless the test class is
 * annotated with
 * [@TestInstance(Lifecycle.PER_CLASS)][org.junit.jupiter.api.TestInstance].
 *
 *
 *
 * The values returned by such methods will be provided as arguments to the
 * annotated `@ParameterizedTest` method.
 *
 * @see ArgumentsSource
 *
 * @see org.junit.jupiter.params.ParameterizedTest
 *
 * @since 5.0
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ArgumentsSource(
    ParametersSourceArgumentsProvider::class
)
@Deprecated(message = "Deprecated in favor of GeneratedParametersTest which can run without @ParameterizedTest", replaceWith = ReplaceWith(expression = "@GeneratedParametersTest"))
annotation class ParametersSource

