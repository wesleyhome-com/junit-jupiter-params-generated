package com.wesleyhome.test.jupiter

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import kotlin.reflect.full.IllegalCallableAccessException

/**
 * The build pins the Kotlin runtime the tests run on: the compiler's own, or the one
 * `-PkotlinRuntime` names. The published code asks for an older one, which would otherwise win.
 */
class KotlinRuntimeTest {

    private val pinned = requireNotNull(System.getProperty("kotlinRuntime")) {
        "Run the tests through Gradle, which sets the Kotlin runtime they expect"
    }

    @Test
    fun testStandardLibraryIsThePinnedVersion() {
        assertThat(KotlinVersion.CURRENT.toString()).isEqualTo(pinned)
    }

    /** Its manifest gives `2.4.21`, or `2.2.21-release-469` for some releases. */
    @Test
    fun testReflectIsThePinnedVersion() {
        assertThat(IllegalCallableAccessException::class.java.`package`.implementationVersion?.substringBefore('-'))
            .isEqualTo(pinned)
    }
}
