buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        //  classpath("com.android.tools.build:gradle:7.2.2") // Use the latest compatible version
        // Ensure the Kotlin plugin is included, matching the version used in your modules
        classpath(kotlin("gradle-plugin", version = "2.1.0"))
        classpath("com.guardsquare:proguard-gradle:7.5.0")

    }
}

plugins {
    kotlin("jvm") version "2.1.0"
}

group = "com.sautner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()

}

dependencies {
    //testImplementation(kotlin("test"))
}


