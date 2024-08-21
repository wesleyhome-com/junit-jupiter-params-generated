/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */
package com.wesleyhome.test.jupiter

import org.junit.jupiter.params.provider.ArgumentsSource
import kotlin.reflect.KClass

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
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(
    AnnotationRetention.RUNTIME
)
@MustBeDocumented
@ArgumentsSource(
    MethodArgumentsProvider::class
)
@Deprecated(message = "", replaceWith = ReplaceWith("com.wesleyhome.test.jupiter.annotations.ParametersSource"))
annotation class ParametersSource(
    /**
     * Defines Classes that contain methods for providing arrays of data.
     *
     * @return the declared provider classes
     */
    vararg val value: KClass<*> = []
)
