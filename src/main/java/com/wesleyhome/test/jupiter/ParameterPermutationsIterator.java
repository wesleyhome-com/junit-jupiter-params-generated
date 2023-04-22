package com.wesleyhome.test.jupiter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.params.provider.Arguments;
import static java.lang.reflect.Modifier.isStatic;

public class ParameterPermutationsIterator implements Iterator<Arguments> {

  private int currentPermutation = 0;

  protected int[] argumentIndexPointers;
  protected final Object testObject;
  protected final Object[][] argumentsParametersArray;
  protected final Method filterMethod;
  protected final long totalPermutations;

  ParameterPermutationsIterator(
    List<Class<?>> parameterOptions,
    Object testObject,
    List<Class<?>> dataProviderClasses,
    String testMethodName
  ) {
    this.testObject = testObject;
    final Class<?> testObjectClass = testObject.getClass();
    filterMethod = ReflectionHelper.getMethodStream(testObjectClass)
      .filter(method -> {
        final String methodName = method.getName();
        final Class<?> returnType = method.getReturnType();
        final boolean parametersEqual = Arrays.asList(method.getParameterTypes()).equals(parameterOptions);
        final boolean returnBoolean = Boolean.class.equals(returnType) || Boolean.TYPE.equals(returnType);
        final boolean containsFilterMethodName = methodName.equals(testMethodName + "_filter");
        return parametersEqual && returnBoolean && containsFilterMethodName;
      }).findFirst().orElse(null);
    if (parameterOptions != null && !parameterOptions.isEmpty()) {
      List<Object[]> options = new ArrayList<>();
      for (int i = 0; i < parameterOptions.size(); i++) {
        Class<?> optionClass = parameterOptions.get(i);
        if (optionClass == null) {
          throw new NullPointerException("Parameters cannot be null");
        }
        if (optionClass.isEnum()) {
          EnumSet<?> enumSet = allOf(optionClass);
          final List<Object> objects = Arrays.stream(enumSet.toArray()).collect(Collectors.toList());
          if (filterMethod != null && !filterMethod.isAnnotationPresent(NotNull.class)) {
            Annotation[][] parameterAnnotations = filterMethod.getParameterAnnotations();
            Annotation parameterAnnotation = Arrays.stream(parameterAnnotations[i])
              .filter(Objects::nonNull)
              .filter(it -> it.annotationType() != null)
              .filter(it -> it.annotationType().equals(NotNull.class))
              .findFirst()
              .orElse(null);
            if (parameterAnnotation == null) {
              objects.add(null);
            }
          }
          options.add(objects.toArray());
        } else if (Boolean.TYPE.equals(optionClass)) {
          options.add(new Object[] {true, false});
        } else if (Boolean.class.equals(optionClass)) {
          options.add(new Object[] {true, false, null});
        } else {
          // can't figure out the class information
          Object[] optionsFromProviders = getOptionsFromProviders(dataProviderClasses, testObject, optionClass);
          if (optionsFromProviders == null) {
            throw new InvalidParameterException(optionClass, dataProviderClasses);
          }
          options.add(optionsFromProviders);
        }
      }
      /* parameter index *//* argument index */
      argumentsParametersArray = options.toArray(new Object[0][0]);
      totalPermutations = totalPermutations();
      argumentIndexPointers = new int[argumentsParametersArray.length];
      Arrays.fill(argumentIndexPointers, 0);
    } else {
      throw new NullPointerException("Parameters options cannot be null or empty");
    }

  }

  @Override
  public boolean hasNext() {
    if (currentPermutation < totalPermutations) {
      Arguments args = createArgument();
      if (shouldFilter(args)) {
        return true;
      } else {
        increment();
        return hasNext();
      }
    }
    return currentPermutation < totalPermutations;
  }

  private void increment() {
    currentPermutation++;
    incrementIndex(argumentIndexPointers.length - 1);
  }

  @Override
  public Arguments next() {
    try {
      return createArgument();
    } finally {
      increment();
    }
  }

  private long totalPermutations() {
    return Arrays.stream(argumentsParametersArray)
      .map(arr -> arr.length)
      .mapToLong(Integer::longValue)
      .reduce((a1, a2) -> a1 * a2)
      .orElse(0L);
  }

  private static <E extends Enum<E>> EnumSet<E> allOf(Class<?> enumClass) {
    return EnumSet.allOf((Class<E>) enumClass);
  }

  private Object[] getOptionsFromProviders(
    List<Class<?>> dataProviderClasses,
    Object testObject,
    Class<?> optionClass
  ) {
    return dataProviderClasses.stream()
      .map(providerClass -> this.getOptionsFromProvider(providerClass, testObject, optionClass))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(null);
  }

  private Object[] getOptionsFromProvider(Class<?> providerClass, Object testObject, Class<?> optionClass) {
    return ReflectionHelper.getMethodStream(providerClass)
      .filter(method -> isStaticOrTestInstanceMethod(method, testObject))
      .filter(method -> isArrayMethodForClass(method, optionClass))
      .map(method -> invokeMethod(method, testObject))
      .findFirst()
      .orElse(null);
  }

  private boolean isArrayMethodForClass(Method method, Class<?> optionClass) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length > 0) {
      return false;
    }
    Class<?> returnType = method.getReturnType();
    boolean isArray = returnType.isArray();
    if (isArray) {
      Class<?> componentType = returnType.getComponentType();
      return componentType.equals(optionClass);
    }
    return false;
  }

  private boolean isStaticOrTestInstanceMethod(Method method, Object testInstance) {
    boolean aStatic = isStaticMethod(method);
    boolean testInstanceMethod = isTestInstanceMethod(method, testInstance);
    return aStatic || testInstanceMethod;
  }

  private boolean isTestInstanceMethod(Method method, Object testInstance) {
    return method.getDeclaringClass().isAssignableFrom(testInstance.getClass());
  }

  private boolean isStaticMethod(Method method) {
    return isStatic(method.getModifiers());
  }

  private Object[] invokeMethod(Method method, Object testObject) {
    Object invokeOn = isStaticMethod(method) ? null : testObject;
    return unpack(ReflectionHelper.invoke(method, invokeOn));
  }

  private static Object[] unpack(Object array) {
    Object[] array2 = new Object[Array.getLength(array)];
    for (int i = 0; i < array2.length; i++) {
      array2[i] = Array.get(array, i);
    }
    return array2;
  }

  private void incrementIndex(int index) {
    if (index < 0) {
      return; // Fail quietly
    }
    int parameterIndex = argumentIndexPointers[index] + 1;
    int length = argumentsParametersArray[index].length;
    if (parameterIndex < length) {
      argumentIndexPointers[index] = parameterIndex;
    } else {
      argumentIndexPointers[index] = 0;
      incrementIndex(index - 1);
    }
  }

  private Arguments createArgument() {
    Object[] arguments = new Object[argumentsParametersArray.length];
    for (int argIndex = 0; argIndex < argumentsParametersArray.length; argIndex++) {
      int paramIndex = argumentIndexPointers[argIndex];
      Object[] parameterValues = argumentsParametersArray[argIndex];
      arguments[argIndex] = parameterValues[paramIndex];
    }
    return new MyArguments(arguments);
  }

  private boolean shouldFilter(Arguments arguments) {
    if (filterMethod == null) {
      return true;
    }
    filterMethod.setAccessible(true);
    try {
      return (boolean) filterMethod.invoke(testObject, arguments.get());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
