pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ktor-autorouter"
include("ktor-server")
include("auto-router")
include("auto-router-ksp")

