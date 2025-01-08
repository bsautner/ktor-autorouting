buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.0"))
    }
}
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

plugins {
    kotlin("jvm") version "2.1.0"
}

group = "io.github.bsautner"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17
    }
}
repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }

}


