import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "com.wesleyhome.test"
var versionString = providers.gradleProperty("version").get()
version = versionString
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

subprojects {
    val subProjectName = name
    if (!subProjectName.contains("examples")) {
        apply(plugin = "org.jetbrains.dokka")
        tasks.register<Jar>("javadocJar") {
            dependsOn(tasks.dokkaJavadoc)
            from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
            archiveClassifier.set("javadoc")
        }
        if (!subProjectName.contains("junit-jupiter-params-generated")) {
            tasks.withType<DokkaTaskPartial>().configureEach {
                enabled = false
            }
        } else {
            dependencies {
                dokkaHtmlPlugin("org.jetbrains.dokka:versioning-plugin:2.0.0")
            }
            tasks.withType<DokkaTaskPartial>().configureEach {
                dokkaSourceSets {
                    configureEach {
                        val paths = "$rootDir/examples/src/main/kotlin/examples/Examples.kt"
                        samples.from(paths)
                        displayName = subProjectName.split("-").map { it.capitalized() }.joinToString(separator = " ")
                    }
                }
            }
        }
    }
}
fun isOnCIServer() = System.getenv("CI") == "true"
tasks.dokkaHtmlMultiModule {
    includes.from("README.md")
    outputDirectory.set(file("docs"))
}

tasks.register("clean") {
    delete("build")
    delete("docs")
}


repositories { mavenCentral() }

nexusPublishing {
    this.repositories {
        sonatype()
    }
}

task("release") {
    doFirst {
        val currentVersion = versionString.toString()
        val isSnapshot = currentVersion.endsWith("-SNAPSHOT")
        val nextVersion = if (isSnapshot) {
            currentVersion.removeSuffix("-SNAPSHOT")
        } else {
            val versionParts = currentVersion.split(".")
            val lastVersion = versionParts.last()
            versionParts.subList(0, versionParts.size - 1)
                .plus((lastVersion.toInt() + 1).toString())
                .joinToString(separator = ".")
        }
        versionString = nextVersion
        releaseVersion(nextVersion)
    }
}

fun releaseVersion(version: String) {
    val propsFile = File("gradle.properties")
    propsFile.bufferedWriter().use {
        it.write("version=$version%n".format())
    }
}

task<Copy>("updateReadme") {
    println(versionString)
    doNotTrackState("This task does not support up-to-date checks.")
    from("README.template.md")
    into(".")
    rename { "README.md" }
    expand(mapOf("version" to versionString))
}
