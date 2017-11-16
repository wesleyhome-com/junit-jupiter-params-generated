package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.provider.Arguments;

import java.util.*;

class ParameterPermutationsIterator implements Iterator<Arguments> {


    private final Object[][] argumentsParametersArray;
    private int[] argumentIndexPointers;
    private long totalPermutations;
    private int currentPermutation = 0;

    ParameterPermutationsIterator(Object[][] argumentsParametersArray) {
        this.argumentsParametersArray = argumentsParametersArray;
        init();
    }

    ParameterPermutationsIterator(List<Object> parameterOptions) {
        if (parameterOptions != null && !parameterOptions.isEmpty()) {
            List<Object[]> options = new ArrayList<>();
            for (Object parameterOption : parameterOptions) {
                if (parameterOption == null) {
                    throw new NullPointerException("Parameters cannot be null");
                }
                if (Boolean.TRUE.equals(parameterOption) || Boolean.FALSE.equals(parameterOption)) {
                    parameterOption = Boolean.TYPE;
                }
                if (parameterOption instanceof Class<?>) {
                    Class<?> optionClass = (Class<?>) parameterOption;
                    if (optionClass.isEnum()) {
                        EnumSet<?> enumSet = allOf(optionClass);
                        options.add(enumSet.toArray());
                    } else if (Boolean.TYPE.equals(optionClass)) {
                        options.add(new Object[]{true, false});
                    } else if (Boolean.class.equals(optionClass)) {
                        options.add(new Object[]{true, false, null});
                    } else {
                        // can't figure out the class information
                        options.add(new Object[]{null});
                    }
                } else {
                    Class<?> optionClass = parameterOption.getClass();
                    if (optionClass.isArray()) {
                        Object[] arr = (Object[]) parameterOption;
                        options.add(arr);
                    } else {
                        // can't figure out the class information
                        options.add(new Object[]{null});
                    }
                }
            }
            /* parameter index *//* argument index */
            argumentsParametersArray = options.toArray(new Object[0][0]);
            init();
        } else {
            throw new NullPointerException("Parameters options cannot be null or empty");
        }
    }

    private void init() {
        argumentIndexPointers = new int[argumentsParametersArray.length];
        Arrays.fill(argumentIndexPointers, 0);
        totalPermutations = Arrays.stream(argumentsParametersArray)
                .map(arr -> arr.length)
                .mapToLong(integer -> integer.longValue())
                .reduce((a1, a2) -> a1 * a2)
                .getAsLong();
    }

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
