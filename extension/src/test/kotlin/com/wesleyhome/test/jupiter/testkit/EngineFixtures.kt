package com.wesleyhome.test.jupiter.testkit

import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod
import org.junit.platform.testkit.engine.EngineExecutionResults
import org.junit.platform.testkit.engine.EngineTestKit

/**
 * Runs one method of a fixture class through a real Jupiter engine and reports what happened.
 *
 * Fixture classes are named `*Fixture` and excluded from the test task, so nothing runs them except
 * a call to one of these helpers. That is what lets a fixture fail on purpose, which is the only way
 * to assert on what a user actually sees when an annotation is misconfigured.
 */
internal fun executeFixture(
    fixture: Class<*>,
    methodName: String,
    vararg configurationParameters: Pair<String, String>
): EngineExecutionResults {
    val method = fixture.declaredMethods.firstOrNull { it.name == methodName }
        ?: error("No method named [$methodName] on ${fixture.name}")
    var builder = EngineTestKit.engine("junit-jupiter").selectors(selectMethod(fixture, method))
    for ((key, value) in configurationParameters) {
        builder = builder.configurationParameter(key, value)
    }
    return builder.execute()
}

/**
 * The display name of every invocation that ran and passed, in execution order.
 *
 * A fixture declaring `name = "{arguments}"` names each invocation after the value it was given, so
 * this doubles as an assertion on the exact sequence of generated values - observed through the same
 * output a user reads in a test report rather than through shared mutable state.
 */
internal fun invocationNames(
    fixture: Class<*>,
    methodName: String,
    vararg configurationParameters: Pair<String, String>
): List<String> =
    executeFixture(fixture, methodName, *configurationParameters)
        .testEvents()
        .succeeded()
        .list()
        .map { it.testDescriptor.displayName }

/** How many invocations started and how many passed. */
internal fun invocationCounts(
    fixture: Class<*>,
    methodName: String,
    vararg configurationParameters: Pair<String, String>
): Pair<Long, Long> {
    val events = executeFixture(fixture, methodName, *configurationParameters).testEvents()
    return events.started().count() to events.succeeded().count()
}

/**
 * The failure a fixture produced, whether it surfaced against the template container - which is
 * where generation errors land, since they happen before any invocation exists - or against an
 * individual invocation.
 */
internal fun executionFailure(
    fixture: Class<*>,
    methodName: String,
    vararg configurationParameters: Pair<String, String>
): Throwable {
    val results = executeFixture(fixture, methodName, *configurationParameters)
    val events = results.allEvents().failed().list()
    return events
        .mapNotNull { it.getPayload(TestExecutionResult::class.java).orElse(null) }
        .mapNotNull { it.throwable.orElse(null) }
        .firstOrNull()
        ?: error("Expected [${fixture.name}#$methodName] to fail, but nothing failed")
}
