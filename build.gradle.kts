buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = "2.1.0"))
    }
}

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

group = "com.sautner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
}



