
plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.10"
    signing
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

group = "com.wesleyhome.test"
val versionString = providers.gradleProperty("version").get()
version = versionString
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    val junitJupiterVersion = "5.11.0"
    api("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    api("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    api("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    implementation("io.github.classgraph:classgraph:4.8.175")
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib"))
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.12.0")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        val description = """
            Library to help generate test parameter permutations for parameterized tests in JUnit.
            This version is an initial attempt to convert to building with Gradle.
        """.trimIndent()

        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                this.description.set(description)
                this.name.set("Generated JUnit Jupiter Parameters")
                this.url.set("https://github.com/justin-wesley/junit-jupiter-params-generated")
                scm {
                    connection.set("scm:git:https://github.com/justin-wesley/junit-jupiter-params-generated.git")
                    developerConnection.set("scm:git:https://github.com/justin-wesley/junit-jupiter-params-generated.git")
                    url.set("https://github.com/justin-wesley/junit-jupiter-params-generated.git")
                    tag.set("HEAD")
                }
                developers {
                    developer {
                        id.set("justin")
                        name.set("Justin Wesley")
                        roles.set(listOf("Software Development Engineer"))
                    }
                }
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

signing {
    setRequired { !project.version.toString().endsWith("-SNAPSHOT") && !project.hasProperty("skipSigning") }
    if (isOnCIServer()) {
        val signingKey: String? by project
        if ((signingKey?.length ?: 0) <= 0) {
            throw RuntimeException("No Signing Key")
        }
        useInMemoryPgpKeys(signingKey, "")
    }
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
fun isOnCIServer() = System.getenv("CI") == "true"
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
