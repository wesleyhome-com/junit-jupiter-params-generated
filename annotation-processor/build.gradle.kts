/*
 * This file was generated by the Gradle 'init' task.
 */
plugins {
    id("buildlogic.kotlin-common-conventions")
}

dependencies {
    implementation(project(":validation"))
    implementation(project(":junit-jupiter-params-generated"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.20-1.0.31")
}
