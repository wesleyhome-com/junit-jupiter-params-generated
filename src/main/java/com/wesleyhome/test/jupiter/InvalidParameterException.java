package com.wesleyhome.test.jupiter;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class InvalidParameterException extends RuntimeException {

  public InvalidParameterException(Class<?> parameterClass, List<Class<?>> dataProviderClasses) {
    super(String.format("Unable to find suitable data provider for %s from %s",
      parameterClass.getSimpleName(),
      dataProviderClasses.stream()
        .map(Class::toString)
        .collect(Collectors.joining(", "))
    ));
  }
}
