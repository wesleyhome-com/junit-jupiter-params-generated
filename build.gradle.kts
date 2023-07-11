plugins {
  java
  `java-library`
  `maven-publish`
  kotlin("jvm") version "1.8.20"
  signing
  id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
  id("net.researchgate.release") version "3.0.2"
}

group = "com.wesleyhome.test"
version = "2.0.1-SNAPSHOT"
description = "junit-jupiter-params-generated"
extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")
repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  api("org.junit.jupiter:junit-jupiter-params:5.9.2")
  api("org.junit.jupiter:junit-jupiter-engine:5.9.2")
  api("org.junit.jupiter:junit-jupiter-api:5.9.2")
  implementation(kotlin("reflect"))
  testImplementation("org.mockito:mockito-core:5.3.1")
  testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
  testImplementation("io.mockk:mockk:1.13.5")
  testImplementation("org.assertj:assertj-core:3.24.2")
}

java {
  withSourcesJar()
  withJavadocJar()
  sourceCompatibility = JavaVersion.VERSION_11
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

kotlin {
  jvmToolchain(11)
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
  sign(publishing.publications["mavenJava"])
}

tasks.withType<Sign>().configureEach {
  onlyIf("isReleaseVersion is set") { (project.extra["isReleaseVersion"] as Boolean) }
}

tasks.javadoc {
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}

nexusPublishing {
  repositories {
    sonatype()
  }
}

release {
  with(git) {
    requireBranch.set("master")
  }
}
