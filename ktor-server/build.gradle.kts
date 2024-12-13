val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project
val serialization_version: String by project
plugins {
    kotlin("jvm")
    application
    id("io.ktor.plugin") version "3.0.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

group = "com.sautner"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}
repositories {
    mavenCentral()
}

ksp {
    arg("RouterClassName", "AutoRouter")
    arg("RouterPackage", "com.sautner.ktor")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":auto-router"))
    ksp(project(":auto-router"))
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder:$ktor_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
