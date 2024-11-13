import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "com.wesleyhome.test"
val versionString = providers.gradleProperty("version").get()
version = versionString
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

subprojects {
    val subProjectName = name
    if(!subProjectName.contains("examples")) {
        apply(plugin = "org.jetbrains.dokka")
        tasks.register<Jar>("javadocJar") {
            dependsOn(tasks.dokkaJavadoc)
            from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
            archiveClassifier.set("javadoc")
        }
        if (!subProjectName.contains("extension")) {
            tasks.withType<DokkaTaskPartial>().configureEach {
                enabled = false
            }
        } else {
            val dokkaPlugin by configurations
            dependencies {
                dokkaPlugin("org.jetbrains.dokka:versioning-plugin:1.9.20")
            }
            tasks.dokkaHtml.configure {

            }
            tasks.withType<DokkaTaskPartial>().configureEach {
                dokkaSourceSets {
                    configureEach {
                        val paths = "$rootDir/examples/src/main/kotlin/examples/Examples.kt"
                        samples.from(paths)
                        includes.from("Module.md")
                        displayName = subProjectName.split("-").map { it.capitalized() }.joinToString(separator = " ")
                    }
                }
            }
        }
    }
}
tasks.dokkaHtmlMultiModule {
    includes.from("README.md")
}

tasks.register("clean") {
    delete("build")
}


repositories { mavenCentral() }

nexusPublishing {
    this.repositories {
        sonatype()
    }
}

task("release") {
    doLast {
        val version = versionString.removeSuffix("-SNAPSHOT")
        val versionParts = version.split(".")
        val lastVersion = versionParts.last()
        val nextVersion = providers.environmentVariable("next-version")
            .orElse(
                versionParts.subList(0, versionParts.size - 1)
                    .plus((lastVersion.toInt() + 1).toString())
                    .joinToString(separator = ".", postfix = "-SNAPSHOT")
            ).get()
        releaseVersion(version)
        releaseVersion(nextVersion)
        exec {
            commandLine("cmd", "/c", "git", "push", "origin", "--tags")
        }

    }
}
fun releaseVersion(version: String) {
    val propsFile = File("gradle.properties")
    propsFile.bufferedWriter().use {
        it.write("version=$version%n".format())
    }
    val isSnapshot = version.endsWith("-SNAPSHOT")
    val action = if (isSnapshot) {
        "Creating new snapshot"
    } else {
        "Releasing Version"
    }
    exec {
        commandLine("cmd", "/c", "git", "add", "gradle.properties")
    }
    exec {
        commandLine("cmd", "/c", "git", "commit", "-m", """"$action $version"""")
    }
    if (!isSnapshot) {
        exec {
            commandLine("cmd", "/c", "git", "tag", """"junit-jupiter-params-generated-$version"""")
        }
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
