package com.wesleyhome.test.jupiter

import org.junit.jupiter.api.extension.ExtensionContext
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/**
 * The clock date and time sources read "now" from.
 *
 * Sources that take their bounds as offsets resolve those against the current instant, so with a
 * system clock the values they generate slide with wall-clock time and a failure found today need
 * not reproduce tomorrow. Fixing the clock makes them reproducible.
 */
object GeneratedParametersClock {

    /** An ISO-8601 instant, for example `2024-01-01T00:00:00Z`. */
    const val FIXED_AT_PROPERTY: String = "com.wesleyhome.test.jupiter.clock.fixed.at"

    /** Zone the fixed clock reports, for the sources that need one to derive a local date or time. */
    const val ZONE_PROPERTY: String = "com.wesleyhome.test.jupiter.clock.zone"

    private val NAMESPACE: ExtensionContext.Namespace =
        ExtensionContext.Namespace.create(GeneratedParametersClock::class.java)

    private const val KEY: String = "clock"

    /**
     * Sets the clock for [context] and everything below it, which is how a test class or a
     * registered extension overrides what the configuration parameters say.
     */
    fun set(context: ExtensionContext, clock: Clock) {
        context.getStore(NAMESPACE).put(KEY, clock)
    }

    internal fun resolve(context: ExtensionContext): Clock =
        context.getStore(NAMESPACE).get(KEY, Clock::class.java)
            ?: fromConfiguration(context)
            ?: Clock.systemUTC()

    private fun fromConfiguration(context: ExtensionContext): Clock? {
        val fixedAt = context.getConfigurationParameter(FIXED_AT_PROPERTY).orElse(null) ?: return null
        val instant = runCatching { Instant.parse(fixedAt.trim()) }.getOrElse {
            throw IllegalArgumentException("$FIXED_AT_PROPERTY must be an ISO-8601 instant, but was [$fixedAt]")
        }
        val zoneId = context.getConfigurationParameter(ZONE_PROPERTY).orElse(null)?.trim()
        val zone = runCatching { zoneId?.let(ZoneId::of) ?: ZoneId.of("UTC") }.getOrElse {
            throw IllegalArgumentException("$ZONE_PROPERTY must be a zone id, but was [$zoneId]")
        }
        return Clock.fixed(instant, zone)
    }
}
