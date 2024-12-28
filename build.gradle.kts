import org.gradle.kotlin.dsl.assign
import org.jetbrains.dokka.DokkaGenerator
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("net.researchgate.release") version "3.1.0"
    id("org.jetbrains.dokka")
}

group = "com.wesleyhome.test"
var versionString = providers.gradleProperty("version").get()
version = versionString
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")


release {
    tagTemplate = "'$name-$version'"
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
