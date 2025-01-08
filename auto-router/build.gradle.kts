val ksp_version: String by project
val poet_version: String by project
val ktor_version: String by project
val serialization_version: String by project

group = "io.github.bsautner"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    `maven-publish`
    `java-library`
    signing
}

repositories {
    mavenCentral()
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.named("javadoc"))
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

//kotlin {
//    js {
//        nodejs()  // Optional, for Node.js support
//        binaries.executable()
//    }
//}

sourceSets {
    val jsMain by creating {
        kotlin.srcDir("src/jsMain/kotlin")
        resources.srcDir("src/jsMain/resources")
    }
}

dependencies {

    implementation("io.ktor:ktor-server-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")
    testImplementation(kotlin("test"))
}

publishing {
    repositories {
        maven {
            name = "localManual"
            url = uri("$buildDir/repo")
        }
    }

    publications {
        create<MavenPublication>("auto-router") {
            from(components["java"])
            artifactId = "auto-router"
            groupId = "io.github.bsautner"
            version = "0.0.1"

            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name.set("Ktor Auto Router")
                description.set("Kotlin Ktor Auto Router")
                url.set("https://github.com/bsautner/ktor-autorouting")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                scm {
                    connection.set("scm:git:git://home/bsautner/ktor-autorouting.git")
                    developerConnection.set("scm:git:ssh://home:bsautner/ktor-autorouting")
                    url.set("https://github.com/bsautner/ktor-autorouting")
                }
                developers {
                    developer {
                        id.set("bsautner")
                        name.set("Benjamin Sautner")
                        email.set("bsautner@gmail.com")
                    }
                }
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")?.replace("\\\\n", "\\n")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["auto-router"])
}
