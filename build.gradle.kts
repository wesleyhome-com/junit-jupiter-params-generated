import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("net.researchgate.release") version "3.1.0" apply false
    id("org.jetbrains.dokka")
    id("com.github.ben-manes.versions") version "0.54.0"
}

group = "com.wesleyhome.test"
var versionString = providers.gradleProperty("version").get()
version = versionString
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")

val isReleaseInvocation = gradle.startParameter.taskNames
    .any { it.substringAfterLast(":").contains("release", ignoreCase = true) }

if (isReleaseInvocation) {
    apply(plugin = "net.researchgate.release")
}

pluginManager.withPlugin("net.researchgate.release") {
    extensions.configure<net.researchgate.release.ReleaseExtension>("release") {
        tagTemplate = "v${'$'}version"
        preTagCommitMessage = "[Gradle Release Plugin] - pre tag commit: v${'$'}version"
        newVersionCommitMessage = "[Gradle Release Plugin] - new version commit: v${'$'}version"
    }

    tasks.named("afterReleaseBuild") {
        dependsOn("build", "dokkaGenerate", "publishProjectToMavenCentral")
    }
}

dependencies {
    dokka(project(":junit-jupiter-params-generated"))
}

dokka {
    pluginsConfiguration {
        html {
            customAssets.from(projectDir.resolve("dokka-assets/version-selector.js"))
        }
    }
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

tasks.register("publishProjectToMavenCentral") {
    group = "publishing"
    description = "Publishes all release modules to Maven Central."
    dependsOn(
        listOf(
            ":validation:publishAndReleaseToMavenCentral",
            ":annotation-processor:publishAndReleaseToMavenCentral",
            ":junit-jupiter-params-generated:publishAndReleaseToMavenCentral"
        )
    )
}

tasks.register("publishProjectSnapshotsToMavenCentral") {
    group = "publishing"
    description = "Publishes all SNAPSHOT modules to Maven Central."
    dependsOn(
        listOf(
            ":validation:publishToMavenCentral",
            ":annotation-processor:publishToMavenCentral",
            ":junit-jupiter-params-generated:publishToMavenCentral"
        )
    )
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
