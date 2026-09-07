package com.wesleyhome.test.jupiter.generator

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isSameInstanceAs
import com.wesleyhome.test.jupiter.provider.number.IntRangeDataProvider
import com.wesleyhome.test.jupiter.provider.number.LongRangeDataProvider
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DataProviderRegistryTest {

    @Test
    fun testSameKeyReturnsTheCachedInstance() {
        val first = DataProviderRegistry.createInstance("cache.key.Single", IntRangeDataProvider::class)
        val second = DataProviderRegistry.createInstance("cache.key.Single", IntRangeDataProvider::class)
        assertThat(second).isSameInstanceAs(first)
    }

    /**
     * The registry is shared by every test method in the JVM, and parallel engine threads reach it
     * at the same time. Each key must still resolve to one instance and no lookup may fail.
     */
    @Test
    fun testConcurrentRegistrationIsConsistent() {
        val keys = (0 until 64).map { "concurrent.key.$it" }
        val threads = 16
        val pool = Executors.newFixedThreadPool(threads)
        try {
            val tasks = (0 until threads).map {
                Callable {
                    keys.map { key ->
                        key to DataProviderRegistry.createInstance(key, LongRangeDataProvider::class)
                    }
                }
            }
            val results = pool.invokeAll(tasks).map { it.get(30, TimeUnit.SECONDS) }
            assertThat(results).hasSize(threads)
            val expected = results.first().toMap()
            val divergent = results.flatten().filter { (key, provider) -> expected[key] !== provider }
            assertThat(divergent).isEmpty()
        } finally {
            pool.shutdownNow()
        }
    }
}
