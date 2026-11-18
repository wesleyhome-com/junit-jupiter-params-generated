plugins {
    java
    jacoco
}

jacoco {
    toolVersion = the<VersionCatalogsExtension>().named("libs").findVersion("jacoco").get().requiredVersion
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    extensions.configure<JacocoTaskExtension> {
        // The agent instruments every class it loads by default. The annotation processor's tests
        // run the Kotlin compiler in-process, and instrumenting it made them about 40% slower.
        includes = listOf("com.wesleyhome.*")
    }
}

// The jacoco plugin only orders these after test, so on their own they would skip, or read stale
// execution data, rather than run the tests.
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.test)
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
