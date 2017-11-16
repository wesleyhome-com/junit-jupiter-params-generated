/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

/**
 * {@code @ParametersSource} is an {@link ArgumentsSource} which provides access
 * to values returned by {@linkplain #value() methods} of the class in
 * which this annotation is declared.
 * <p>
 * <p>By default such methods must be {@code static} unless the test class is
 * annotated with
 * {@link org.junit.jupiter.api.TestInstance @TestInstance(Lifecycle.PER_CLASS)}.
 * <p>
 * <p>The values returned by such methods will be provided as arguments to the
 * annotated {@code @ParameterizedTest} method.
 *
 * @see ArgumentsSource
 * @see org.junit.jupiter.params.ParameterizedTest
 * @since 5.0
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ArgumentsSource(MethodArgumentsProvider.class)
public @interface ParametersSource {

    /**
     * The names of the test class methods to use as sources for arguments; must
     * not be empty.
     */
//	String[] value();

}
