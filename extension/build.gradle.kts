/*
 * This file was generated by the Gradle 'init' task.
 */
plugins {
    id("buildlogic.kotlin-common-conventions")
    jacoco
    id("org.jetbrains.dokka")
}

dependencies {
    api(libs.bundles.junit)
    api(project(":validation"))
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation("org.mockito:mockito-core:5.14.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.14.2")
    testImplementation("io.mockk:mockk:1.13.14")
    testImplementation("org.assertj:assertj-core:3.27.1")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
}

dokka {
    dokkaSourceSets {
        main {
            val paths = "$rootDir/examples/src/main/kotlin/examples/Examples.kt"
            samples.from(paths)
        }
    }
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
