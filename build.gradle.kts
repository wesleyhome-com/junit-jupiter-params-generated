import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("org.jetbrains.dokka") version "2.0.0"
    id("net.researchgate.release") version "3.1.0"
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
            tasks.withType<DokkaTaskPartial>().configureEach {
                dokkaSourceSets {
                    configureEach {
                        val paths = "$rootDir/examples/src/main/kotlin/examples/Examples.kt"
                        samples.from(paths)
                        displayName = subProjectName.split("-").joinToString(separator = " ") { it.capitalized() }
                    }
                }
            }
        }
    }
}

release {
    tagTemplate = "'$name-$version'"

}



tasks.dokkaHtmlMultiModule {
    includes.from("README.md")
    outputDirectory = projectDir.resolve("docs")
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
