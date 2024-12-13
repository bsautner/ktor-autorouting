val ksp_version: String by project
val poet_version: String by project
val ktor_version: String by project
val serialization_version: String by project
plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

group = "com.sautner"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}



dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:$ksp_version")
    implementation("com.squareup:kotlinpoet:$poet_version")
    implementation("com.squareup:kotlinpoet-ksp:$poet_version")
    implementation("io.ktor:ktor-server-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation(kotlin("test"))
}

