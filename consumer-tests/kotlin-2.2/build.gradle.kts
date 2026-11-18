import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

// A Kotlin 2.2 project's test setup, using the library this repository publishes.
// .github/scripts/consumer-smoke-test.sh publishes it and runs this build against it, once with
// Spring Boot's dependency management (-PspringBoot=4.0.9) and once without.
plugins {
    kotlin("jvm") version "2.2.21"
    // The oldest KSP the annotation processor supports. It runs KSP2, as later releases do.
    id("com.google.devtools.ksp") version "2.2.21-2.0.4"
    id("io.spring.dependency-management") version "1.1.7" apply false
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

providers.gradleProperty("springBoot").orNull?.let { springBoot ->
    apply(plugin = "io.spring.dependency-management")
    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBoot")
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("com.wesleyhome.test:junit-jupiter-params-generated:$libraryVersion")
    kspTest("com.wesleyhome.test:annotation-processor:$libraryVersion")
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("kotlinRuntime", providers.gradleProperty("kotlinRuntime").get())
    providers.gradleProperty("junitVersion").orNull?.let { systemProperty("junitVersion", it) }
}
