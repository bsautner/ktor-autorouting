val ksp_version: String by project
val poet_version: String by project
val ktor_version: String by project
val serialization_version: String by project

group = "io.github.bsautner"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.google.devtools.ksp")
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
    // If you're building a Kotlin-only project without Java docs, you can skip actual generation
    // or replace with Dokka if you want real Kotlin docs.
    from(tasks.named("javadoc"))
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    // If your source is in `src/main/kotlin`, include that
    from(sourceSets["main"].allSource)
}

dependencies {
    implementation(project(":auto-router"))
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

publishing {
    repositories {
        maven {
            name = "localManual"
            // Publish to build/repo (or any local directory you want)
            url = uri("$buildDir/repo")
        }
    }

    publications {
        create<MavenPublication>("auto-router-ksp") {
            from(components["java"])
            artifactId = "auto-router-ksp"
            groupId = "io.github.bsautner"
            version = "0.0.1"

            // Attach sources and javadoc jars
            artifact(sourcesJar)
            artifact(javadocJar)

            pom {
                name.set("Ktor Auto Router KSP")
                description.set("Kotlin Ktor Auto Router Code Generator")
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
    val signingKey = System.getenv("SIGNING_KEY")?.replace("\\n", "\n")
    val signingPassword = System.getenv("SIGNING_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["auto-router-ksp"])
}
