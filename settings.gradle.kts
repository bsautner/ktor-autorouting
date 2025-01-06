pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ktor-autorouter"
include("auto-router")
include("auto-router-ksp")
include("auto-router-example")

