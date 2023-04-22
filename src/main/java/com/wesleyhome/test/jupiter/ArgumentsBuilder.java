package com.wesleyhome.test.jupiter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

public class ArgumentsBuilder {

  private final List<Class<?>> arguments;
  private final Object testInstance;
  private final List<Class<?>> dataProviderClasses;
  private final String testMethodName;

  static ArgumentsBuilder create(Object testInstance, Class<?> firstArgument, String testMethodName) {
    return new ArgumentsBuilder(testInstance, firstArgument, new ArrayList<>(), testMethodName);
  }

  static Stream<? extends Arguments> create(ExtensionContext extensionContext) {
    Method requiredTestMethod = extensionContext.getRequiredTestMethod();
    Class<?> requiredTestClass = extensionContext.getRequiredTestClass();
    Object testInstance = ReflectionHelper.invokeConstructor(requiredTestClass);
    final ParametersSource parametersSource = requiredTestMethod.getAnnotation(ParametersSource.class);
    Class<?>[] dataProviderClasses = parametersSource.value();
    Class<?>[] parameterTypes = requiredTestMethod.getParameterTypes();
    String methodName = requiredTestMethod.getName();
    List<Class<?>> dataProviderList = Stream.concat(Stream.of(requiredTestClass), Stream.of(dataProviderClasses))
      .collect(Collectors.toList());
    ArgumentsBuilder builder = new ArgumentsBuilder(testInstance, dataProviderList, methodName);
    Stream.of(parameterTypes)
      .forEach(builder::arg);
    return builder.build();
  }

  private ArgumentsBuilder(Object testInstance, final List<Class<?>> dataProviderClasses, final String testMethodName) {
    this.testInstance = testInstance;
    this.dataProviderClasses = dataProviderClasses;
    this.testMethodName = testMethodName;
    this.arguments = new ArrayList<>();
  }

  private ArgumentsBuilder(Object testInstance, Class<?> firstArgument, final List<Class<?>> dataProviderClasses,
                           final String testMethodName) {
    this.testInstance = testInstance;
    this.dataProviderClasses = dataProviderClasses;
    this.testMethodName = testMethodName;
    this.arguments = new ArrayList<>();
    this.arguments.add(firstArgument);
  }

  private ArgumentsBuilder arg(Class<?> argument) {
    this.arguments.add(argument);
    return this;
  }

  public Stream<? extends Arguments> build() {
    Iterable<Arguments>
      argumentsIterable =
      () -> new ParameterPermutationsIterator(arguments, testInstance, dataProviderClasses, testMethodName);
    return StreamSupport.stream(argumentsIterable.spliterator(), false);
  }
}
