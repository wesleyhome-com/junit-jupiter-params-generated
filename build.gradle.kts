import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("net.researchgate.release") version "3.2.0" apply false
    id("org.jetbrains.dokka")
    id("io.github.ben-manes.versions") version "0.67.0"
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
        preTagCommitMessage = "[Gradle Release Plugin] - pre tag commit:"
        newVersionCommitMessage = "[Gradle Release Plugin] - new version commit:"
    }

    tasks.named("afterReleaseBuild") {
        dependsOn("build", "dokkaGenerate", "publishProjectToMavenCentral")
    }

    // Runs before anything is built or published, and the release plugin's pre-tag commit
    // (git commit -a) picks up the edited CHANGELOG.md, so the tag carries the dated section.
    val releaseChangelog = tasks.register("releaseChangelog") {
        description = "Renames the [Unreleased] section of CHANGELOG.md to the release version."
        val changelog = layout.projectDirectory.file("CHANGELOG.md").asFile
        val releaseVersion = version.toString()
        doLast {
            check(!releaseVersion.endsWith("SNAPSHOT")) { "Not a release version: $releaseVersion" }
            val text = changelog.readText()
            val unreleased = "## [Unreleased]"
            val entries = text.substringAfter(unreleased, "").substringBefore("\n## ").trim()
            check(entries.isNotEmpty()) {
                "CHANGELOG.md has no entries under $unreleased for $releaseVersion. " +
                    "Add them in a pull request before releasing."
            }
            val compareLink = Regex("""^\[Unreleased]: (.+)/compare/(\S+)\.\.\.HEAD$""", RegexOption.MULTILINE)
            val link = checkNotNull(compareLink.find(text)) { "CHANGELOG.md has no [Unreleased] compare link" }
            val (repository, previousTag) = link.destructured
            val tag = "v$releaseVersion"
            changelog.writeText(
                text.replaceFirst(unreleased, "$unreleased\n\n## [$releaseVersion] - ${java.time.LocalDate.now()}")
                    .replace(
                        link.value,
                        "[Unreleased]: $repository/compare/$tag...HEAD\n" +
                            "[$releaseVersion]: $repository/compare/$previousTag...$tag"
                    )
            )
        }
    }
    tasks.named("beforeReleaseBuild") {
        dependsOn(releaseChangelog)
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
