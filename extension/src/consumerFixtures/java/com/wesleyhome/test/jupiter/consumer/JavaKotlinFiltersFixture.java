package com.wesleyhome.test.jupiter.consumer;

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest;
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource;

/** Java tests using Kotlin filters, compiled as consumers compile both. Driven by ParameterNamesTest; not run directly. */
public class JavaKotlinFiltersFixture extends KotlinConsumerFilters {

    @GeneratedParametersTest(name = "{arguments}", filters = {"kotlinReversed"})
    public void reversedByKotlin(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }

    @GeneratedParametersTest(name = "{arguments}", filters = {"crossRank"})
    public void twoRanks(@IntRangeSource(min = 1, max = 3) int start, @IntRangeSource(min = 1, max = 3) int end) {
    }
}
