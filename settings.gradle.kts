/*
 * This file was generated by the Gradle 'init' task.
 */
plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

val name = "junit-jupiter-params-generated"
rootProject.name = "$name-parent"
val subProjects = listOf("annotations", "validation", "annotation-processor", "extension", "examples")
include(subProjects)
//subProjects.filterNot { it == "extension" }.forEach {
//    project(":$it").name = "$name-$it"
//}
project(":extension").name = name
