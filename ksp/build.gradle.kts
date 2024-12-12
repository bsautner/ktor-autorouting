val ksp_version: String by project

plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = "com.sautner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$ksp_version")
    implementation("com.squareup:kotlinpoet:1.18.1")

    testImplementation(kotlin("test"))
}

