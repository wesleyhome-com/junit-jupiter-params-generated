package com.wesleyhome.test.jupiter.generator

import com.wesleyhome.test.jupiter.testkit.executeFixture
import org.junit.jupiter.api.Test
import org.junit.platform.engine.TestExecutionResult
import java.io.File

class EmptyProbeTest {
    @Test
    fun probe() {
        val results = executeFixture(ParameterFilterTest.Fixture::class.java, "acceptsNothing")
        val failures = results.allEvents().failed().list()
            .mapNotNull { it.getPayload(TestExecutionResult::class.java).orElse(null) }
            .mapNotNull { it.throwable.orElse(null) }
        val report = buildString {
            appendLine("failures=${failures.size}")
            failures.forEach { appendLine("${it.javaClass.name}: ${it.message}") }
        }
        File("C:/Users/justi/AppData/Local/Temp/probe-out.txt").writeText(report)
    }
}
