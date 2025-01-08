val koin_version: String by project
val kotlin_version: String by project
val kotlinx_html_version: String by project
val logback_version: String by project
val ktor_version: String by project
val ksp_version: String by project

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("io.ktor.plugin") version "3.0.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

group = "io.github.bsautner"
version = "0.0.1"

repositories {
    google()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
    maven { url = uri("https://packages.jetbrains.team/maven/p/kds/kotlin-ds-maven") }
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Use JDK 17
    }
}

ksp {
   // arg("ksp.generated.dir", "$buildDir/generated/ksp/js/jsMain/kotlin") // Keep existing args
    arg("ksp.generated.dir", "$projectDir/src/jsMain/kotlin")

}
kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser {
            webpackTask {
                binaries.executable()
//                cssSupport.enabled = true
            }
        }
//        this@kotlin.explicitApi()

    }
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("src/commonMain/kotlin")
            kotlin.srcDir("$buildDir/generated/ksp/common/commonMain/kotlin")
            dependencies {

                implementation(kotlin("stdlib-common"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("src/jvmMain/kotlin")
            kotlin.srcDir("$buildDir/generated/ksp/jvmMain/kotlin")
            dependencies {
                implementation(project(":auto-router"))



                implementation("io.ktor:ktor-server-core-jvm")
                implementation("io.ktor:ktor-server-resources-jvm")
                implementation("io.ktor:ktor-server-content-negotiation-jvm")
                implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
                implementation("io.ktor:ktor-server-html-builder-jvm")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinx_html_version")
                implementation("io.insert-koin:koin-ktor:$koin_version")
                implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
                implementation("io.ktor:ktor-server-netty-jvm")
                implementation("ch.qos.logback:logback-classic:$logback_version")
                implementation("io.ktor:ktor-server-config-yaml-jvm")

            }
        }
        val jsMain by getting {
            kotlin.srcDir("src/jsMain/kotlin")
//            kotlin.srcDir("$buildDir/generated/ksp/js/jsMain/kotlin")
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

                implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinx_html_version")
            }
        }
    }

}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    add("kspJvm", project(":auto-router-ksp"))
    add("kspCommonMainMetadata", project(":auto-router-ksp"))
    add("kspJs", project(":auto-router-ksp"))

}

//tasks.named("compileKotlinJvm").configure {
//    dependsOn("kspKotlinJvm")
//}
//
//tasks.named("compileKotlinJs").configure {
//    dependsOn("kspKotlinJs")
//
//}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
//    if (name != "kspKotlinJs") {
//        dependsOn("kspKotlinJs")
//    }
//}
//
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
//    if (name != "kspCommonMainKotlinMetadata") {
//        dependsOn("kspCommonMainKotlinMetadata")
//    }
//}
//tasks.withType<com.google.devtools.ksp.gradle.KspTaskJS> {
//    enabled = true
//}
//
//tasks.withType<com.google.devtools.ksp.gradle.KspTaskJvm> {
//    enabled = true
//}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    if (name != "kspKotlinJvm" && name != "kspKotlinJs") {
//        dependsOn(tasks.withType<com.google.devtools.ksp.gradle.KspTaskJvm>())
//    }
//    if (name != "kspKotlinJvm" && name != "kspKotlinJs") {
//        dependsOn(tasks.withType<com.google.devtools.ksp.gradle.KspTaskJS>())
//    }
//
//}
