package com.wesleyhome.test.jupiter;

import com.wesleyhome.common.utilities.ReflectionHelper;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static com.wesleyhome.common.utilities.ReflectionHelper.invoke;

class ParameterPermutationsIterator implements Iterator<Arguments> {


    private final Object[][] argumentsParametersArray;
    private int[] argumentIndexPointers;
    private long totalPermutations;
    private int currentPermutation = 0;

    ParameterPermutationsIterator(List<Class<?>> parameterOptions, Object testObject, List<Class<?>> dataProviderClasses) {
        if (parameterOptions != null && !parameterOptions.isEmpty()) {
            List<Object[]> options = new ArrayList<>();
            for (Class<?> optionClass : parameterOptions) {
                if (optionClass == null) {
                    throw new NullPointerException("Parameters cannot be null");
                }
                if (optionClass.isEnum()) {
                    EnumSet<?> enumSet = allOf(optionClass);
                    options.add(enumSet.toArray());
                } else if (Boolean.TYPE.equals(optionClass)) {
                    options.add(new Object[]{true, false});
                } else if (Boolean.class.equals(optionClass)) {
                    options.add(new Object[]{true, false, null});
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
            init();
        } else {
            throw new NullPointerException("Parameters options cannot be null or empty");
        }
    }

    private Object[] getOptionsFromProviders(List<Class<?>> dataProviderClasses, Object testObject, Class<?> optionClass) {
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
        return Modifier.isStatic(method.getModifiers());
    }

    private Object[] invokeMethod(Method method, Object testObject) {
        Object invokeOn = isStaticMethod(method) ? null : testObject;
        return unpack(invoke(method, invokeOn));
    }

    private static Object[] unpack(Object array) {
        Object[] array2 = new Object[Array.getLength(array)];
        for (int i = 0; i < array2.length; i++)
            array2[i] = Array.get(array, i);
        return array2;
    }

    private void init() {
        argumentIndexPointers = new int[argumentsParametersArray.length];
        Arrays.fill(argumentIndexPointers, 0);
        totalPermutations = Arrays.stream(argumentsParametersArray)
            .map(arr -> arr.length)
            .mapToLong(Integer::longValue)
            .reduce((a1, a2) -> a1 * a2)
            .orElse(0L);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> EnumSet<E> allOf(Class<?> enumClass) {
        return EnumSet.allOf((Class<E>) enumClass);
    }

    @Override
    public boolean hasNext() {
        return currentPermutation < totalPermutations;
    }

    @Override
    public Arguments next() {
        Object[] arguments = new Object[argumentsParametersArray.length];
        for (int argIndex = 0; argIndex < argumentsParametersArray.length; argIndex++) {
            int paramIndex = argumentIndexPointers[argIndex];
            Object[] parameterValues = argumentsParametersArray[argIndex];
            arguments[argIndex] = parameterValues[paramIndex];
        }
        increment();
        return new MyArguments(arguments);
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

    private void increment() {
        currentPermutation++;
        incrementIndex(argumentIndexPointers.length - 1);
    }
}
