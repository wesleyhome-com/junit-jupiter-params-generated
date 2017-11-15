package com.wesleyhome.test.jupiter;

import org.junit.jupiter.params.provider.Arguments;

import java.util.*;

class ParameterPermutationsIterator implements Iterator<Arguments> {


    private final Object[][] argumentsParemetersArray;
    private final int[] argumentIndexPointers;
    private final long totalPermutations;
    private int currentPermutation = 0;

    ParameterPermutationsIterator(Object... parameterOptions) {
        if (parameterOptions != null && parameterOptions.length > 0) {
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
                        continue;
                    }
                    if (Boolean.TYPE.equals(optionClass)) {
                        options.add(new Object[]{true, false});
                        continue;
                    }
                    if (Boolean.class.equals(optionClass)) {
                        options.add(new Object[]{true, false, null});
                        continue;
                    }
                }else {
                    Class<?> optionClass = parameterOption.getClass();
                    if (optionClass.isArray()) {
                        Object[] arr = (Object[]) parameterOption;
                        options.add(arr);
                        continue;
                    }
                    if (optionClass.isEnum()) {
                        EnumSet<?> enumSet = allOf(optionClass);
                        options.add(enumSet.toArray());
                        continue;
                    }
                }
                // if you get here, the parameter is invalid
                throw new IllegalArgumentException(parameterOption + " is an invalid parameter. Must be of type Class<? extends Enum>, Boolean.class, Boolean.TYPE (true or false can be substituted for this), or an array of values");
            }
            /* parameter index *//* argument index */
            argumentsParemetersArray = options.toArray(new Object[0][0]);
            argumentIndexPointers = new int[argumentsParemetersArray.length];
            Arrays.fill(argumentIndexPointers, 0);
            totalPermutations = Arrays.stream(argumentsParemetersArray)
                    .map(arr -> arr.length)
                    .mapToLong(integer -> integer.longValue())
                    .reduce((a1, a2) -> a1 * a2)
                    .getAsLong();
        } else {
            throw new NullPointerException("Parameters options cannot be null or empty");
        }
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
        Object[] arguments = new Object[argumentsParemetersArray.length];
        for (int argIndex = 0; argIndex < argumentsParemetersArray.length; argIndex++) {
            int paramIndex = argumentIndexPointers[argIndex];
            Object[] parameterValues = argumentsParemetersArray[argIndex];
            arguments[argIndex] = parameterValues[paramIndex];
        }
        increment();
        return Arguments.of(arguments);
    }

    private void incrementIndex(int index){
        if(index < 0){
            return; // Fail quietly
        }
        int parameterIndex = argumentIndexPointers[index] + 1;
        int length = argumentsParemetersArray[index].length;
        if(parameterIndex < length) {
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
