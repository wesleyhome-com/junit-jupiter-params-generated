import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("net.researchgate.release") version "3.1.0"
    id("org.jetbrains.dokka")
    id("com.github.ben-manes.versions") version "0.53.0"
}

group = "com.wesleyhome.test"
var versionString = providers.gradleProperty("version").get()
version = versionString
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")


release {
    tagTemplate = "$name-$version"
}

dependencies {
    dokka(project(":junit-jupiter-params-generated"))
}

dokka {
    dokkaPublications {
        html {
            outputDirectory = projectDir.resolve("docs")
            includes.from("README.md")
        }
    }
}

tasks.named("clean") {
    doLast {
        delete("build")
        delete("docs")
    }
}

repositories { mavenCentral() }

nexusPublishing {
    this.repositories {
        sonatype()
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask>().configureEach {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}
