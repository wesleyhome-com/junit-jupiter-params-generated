// A Spring Boot 4.0 project's test setup, compiled by Kotlin 2.2, using the library this repository
// publishes. .github/scripts/consumer-smoke-test.sh publishes it and runs this build against it.
plugins {
    kotlin("jvm") version "2.2.21"
    // The oldest KSP the annotation processor supports. It runs KSP2, as later releases do.
    id("com.google.devtools.ksp") version "2.2.21-2.0.4"
    id("io.spring.dependency-management") version "1.1.7"
}

val libraryRepository = providers.gradleProperty("libraryRepository").get()
val libraryVersion = providers.gradleProperty("libraryVersion").get()

repositories {
    exclusiveContent {
        forRepository { maven(file(libraryRepository)) }
        filter { includeGroup("com.wesleyhome.test") }
    }
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.9")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("com.wesleyhome.test:junit-jupiter-params-generated:$libraryVersion")
    kspTest("com.wesleyhome.test:annotation-processor:$libraryVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
