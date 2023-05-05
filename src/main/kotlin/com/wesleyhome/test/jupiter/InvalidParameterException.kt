package com.wesleyhome.test.jupiter

import java.util.stream.Collectors

class InvalidParameterException(parameterClass: Class<*>, dataProviderClasses: List<Class<*>>) : RuntimeException(
  String.format("Unable to find suitable data provider for %s from %s",
    parameterClass.simpleName,
    dataProviderClasses.stream()
      .map { obj: Class<*> -> obj.toString() }
      .collect(Collectors.joining(", "))
  ))
