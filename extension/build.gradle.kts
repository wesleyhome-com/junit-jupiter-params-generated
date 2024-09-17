/*
 * This file was generated by the Gradle 'init' task.
 */
plugins {
    id("buildlogic.kotlin-common-conventions")
    jacoco
}

dependencies {
    val rootName = rootProject.name.removeSuffix("-parent")
    api(libs.bundles.junit)
    api(project(":${rootName}-annotations"))
    api(project(":${rootName}-validation"))
    implementation("io.github.classgraph:classgraph:4.8.176")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation("org.mockito:mockito-core:5.13.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.13.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        isFailOnViolation = true
        rule {
            element = "METHOD"
            limit {
                maximum = 1.00.toBigDecimal()
                counter = "INSTRUCTION"
                value = "MISSEDCOUNT"
            }
        }
        rule {
            element = "METHOD"
            limit {
                maximum = 1.00.toBigDecimal()
                counter = "BRANCH"
                value = "MISSEDCOUNT"
            }
        }
        rule {
            element = "LINE"
            limit {
                maximum = 1.00.toBigDecimal()
                counter = "INSTRUCTION"
                value = "MISSEDCOUNT"
            }
        }
        rule {
            element = "LINE"
            limit {
                maximum = 1.00.toBigDecimal()
                counter = "BRANCH"
                value = "MISSEDCOUNT"
            }
        }
        rule {
            element = "CLASS"
            limit {
                maximum = 1.00.toBigDecimal()
                counter = "INSTRUCTION"
                value = "MISSEDCOUNT"
            }
        }
        rule {
            element = "CLASS"
            limit {
                maximum = 1.00.toBigDecimal()
                counter = "BRANCH"
                value = "MISSEDCOUNT"
            }
        }
    }
}
