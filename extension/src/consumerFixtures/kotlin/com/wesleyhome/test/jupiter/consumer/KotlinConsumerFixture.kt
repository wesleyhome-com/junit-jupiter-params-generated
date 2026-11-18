package com.wesleyhome.test.jupiter.consumer

import com.wesleyhome.test.jupiter.annotations.GeneratedParametersTest
import com.wesleyhome.test.jupiter.annotations.ValuesFrom
import com.wesleyhome.test.jupiter.annotations.WithNull
import com.wesleyhome.test.jupiter.annotations.number.IntRangeSource
import com.wesleyhome.test.jupiter.annotations.number.IntSource
import com.wesleyhome.test.jupiter.annotations.number.LongSource

/** Compiled without javaParameters, as Kotlin is by default. Driven by ParameterNamesTest; not run directly. */
class KotlinConsumerFixture {

    @GeneratedParametersTest(name = "{arguments}")
    fun jvmStatic(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 3) end: Int) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    fun companionMember(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 3) end: Int) {
    }

    @GeneratedParametersTest(name = "{arguments}", filters = ["endAfterStart"])
    fun reversed(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 3) end: Int) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    fun internalFilter(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 3) end: Int) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    fun valueClass(@LongSource([1, 2, 3]) amount: Cents, @IntRangeSource(min = 1, max = 2) count: Int) {
    }

    @GeneratedParametersTest(name = "{arguments}")
    fun synthetic(@IntRangeSource(min = 1, max = 3) start: Int) {
    }

    @GeneratedParametersTest
    fun displayed(@IntRangeSource(min = 1, max = 2) start: Int, @IntRangeSource(min = 1, max = 2) end: Int) {
    }

    @GeneratedParametersTest(filters = ["mentionsMissing"])
    fun unknownParameter(@IntRangeSource(min = 1, max = 2) start: Int) {
    }

    @GeneratedParametersTest(filters = ["expectsString"])
    fun wrongType(@IntRangeSource(min = 1, max = 2) start: Int) {
    }

    @GeneratedParametersTest(filters = ["expectsPrimitive"])
    fun nullIntoPrimitive(@IntSource([1, 2]) @WithNull start: Int?) {
    }

    @GeneratedParametersTest(filters = ["receiverRule"])
    fun extensionReceiver(@IntRangeSource(min = 1, max = 2) start: Int) {
    }

    @GeneratedParametersTest
    fun valuesFromMissing(@ValuesFrom("nope") label: String) {
    }

    companion object {
        @JvmStatic
        fun jvmStatic_filter(start: Int, end: Int): Boolean = start <= end

        fun companionMember_filter(start: Int, end: Int): Boolean = start <= end

        @JvmStatic
        fun endAfterStart(end: Int, start: Int): Boolean = start <= end

        @JvmStatic
        internal fun internalFilter_filter(start: Int, end: Int): Boolean = start <= end

        @JvmStatic
        fun valueClass_filter(count: Int, amount: Cents): Boolean = amount.value != count.toLong()

        @JvmStatic
        @JvmSynthetic
        fun synthetic_filter(start: Int): Boolean = start <= 2

        @JvmStatic
        fun mentionsMissing(nope: Int): Boolean = true

        @JvmStatic
        fun expectsString(start: String): Boolean = true

        @JvmStatic
        fun expectsPrimitive(start: Int): Boolean = true

        @JvmStatic
        fun Int.receiverRule(): Boolean = this > 1
    }
}

class NamedCompanionConsumerFixture {

    @GeneratedParametersTest(name = "{arguments}")
    fun ordered(@IntRangeSource(min = 1, max = 3) start: Int, @IntRangeSource(min = 1, max = 3) end: Int) {
    }

    companion object Rules {
        fun ordered_filter(end: Int, start: Int): Boolean = start <= end
    }
}

@JvmInline
value class Cents(val value: Long)

/** A top-level class, so ParameterNamesTest can load it again on its own. */
object KotlinConsumerRules {
    fun ordered(end: Int, start: Int): Boolean = start <= end

    @JvmOverloads
    fun upTo(start: Int, end: Int = 2): Boolean = start <= end
}

/** Kotlin filters for Java tests, which bound by position before names were read. */
open class KotlinConsumerFilters {
    companion object {
        @JvmStatic
        fun kotlinReversed(end: Int, start: Int): Boolean = start <= end

        @JvmStatic
        fun crossRank(a: Int): Boolean = a >= 2

        /** Never chosen while the public overload can be: it would filter on `end` instead of `start`. */
        @JvmStatic
        internal fun crossRank(end: Number): Boolean = end.toInt() >= 2

        @JvmStatic
        fun rank(end: Int, other: Int): Boolean = end <= other

        /** Never chosen while the public overload binds; it would filter on `start`. */
        @JvmStatic
        internal fun rank(start: Number): Boolean = start.toInt() >= 2
    }
}

/** A Kotlin interface's default tests reach the extension as bridges in the implementing class. */
interface KotlinConsumerContract {

    @GeneratedParametersTest(name = "{arguments}")
    fun fromInterface(@IntRangeSource(min = 1, max = 3) start: Int) {
    }

    @GeneratedParametersTest(name = "{arguments}", filters = ["keepBig"])
    fun explicitFromInterface(@IntRangeSource(min = 1, max = 3) start: Int) {
    }

    companion object {
        fun fromInterface_filter(start: Int): Boolean = start >= 2

        fun keepBig(start: Int): Boolean = start >= 2
    }
}

class KotlinConsumerImplementing : KotlinConsumerContract
