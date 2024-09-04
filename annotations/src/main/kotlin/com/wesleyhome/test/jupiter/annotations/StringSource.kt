package com.wesleyhome.test.jupiter.annotations

/**
 * Annotation to indicate that the annotated String parameter should be populated with a random value from the provided
 * [values] array.
 *
 * <code>
 *
 *     @ParameterizedTest
 *     @ParameterSource
 *     fun test(@StringSource(["a", "b", "c"]) value: String) {
 *     // test code
 *     }
 *     // will generate 3 tests with the values "a", "b", and "c"
 *     // the values will be used in the order they are provided
 * </code>
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StringSource(val values: Array<String>)
