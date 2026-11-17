plugins {
    java
    jacoco
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    extensions.configure<JacocoTaskExtension> {
        // The agent instruments every class it loads by default. The annotation processor's tests
        // run the Kotlin compiler in-process, and instrumenting it made them about 40% slower.
        includes = listOf("com.wesleyhome.*")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
