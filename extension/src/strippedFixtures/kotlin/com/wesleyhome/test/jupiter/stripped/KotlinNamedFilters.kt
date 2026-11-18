package com.wesleyhome.test.jupiter.stripped

/** Kotlin records parameter names whatever javac is told, so these have names a stripped Java test doesn't. */
open class KotlinNamedFilters {
    companion object {
        @JvmStatic
        fun sharedOrdered(start: Int, end: Int): Boolean = start <= end

        @JvmStatic
        fun expectsText(start: String): Boolean = start.isNotEmpty()
    }
}
