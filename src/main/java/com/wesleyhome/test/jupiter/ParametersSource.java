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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.provider.ArgumentsSource;

/**
 * {@code @ParametersSource} is an {@link ArgumentsSource} which provides access
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
@SuppressWarnings("WeakerAccess")
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ArgumentsSource(MethodArgumentsProvider.class)
public @interface ParametersSource {

  /**
   * Defines Classes that contain methods for providing arrays of data.
   *
   * @return the declared provider classes
   */
  Class<?>[] value() default {};

}
