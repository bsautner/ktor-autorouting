![Maven Central Version](https://img.shields.io/maven-central/v/io.github.bsautner/auto-router)


# ktor-autorouting
An advanced framework for [typesafe routing in ktor](https://ktor.io/docs/server-resources.html) with [KSP Code Generation](https://kotlinlang.org/docs/ksp-overview.html).
[https://central.sonatype.com/artifact/io.github.bsautner/auto-router/overview](https://central.sonatype.com/artifact/io.github.bsautner/auto-router/overview)

## Usage

Add these dependencies to your project.  The code generation will run with your build tasks. 

```
implementation("io.github.bsautner:auto-router:0.0.1")
ksp("io.github.bsautner:auto-router-ksp:0.0.1")

//other ktor server dependencies including the Resources Plugin.
implementation("io.ktor:ktor-server-resources:$ktor_version")

``` 

Add the KSP Plugin to your Plugins section of the root build.gradle in your project.  Make sure you are using a version of ksp that is compatible with your version of Kotlin: 

```aiignore
    plugins {
        kotlin("jvm") version "2.1.0"
        id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    }
```

When you compile your ktor server, this class will be generated in your build folder: 
```aiignore
ktor-server/build/generated/ksp/main/kotlin/io/github/bsautner/autorouter/annotations/AutoRouting.kt
```
You'll want to add that to your path: 

```kotlin
    kotlin {
        sourceSets.main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
    }
```
This is a crisp clean implementation of Type Safe Routing. Your Ktor Server routing plugin usage will always just need to be this simple:

```kotlin
import io.github.bsautner.autorouter.annotations.autoRoute
import io.ktor.server.application.*
import io.ktor.server.resources.*


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Resources)
    autoRoute()
}
```

You can then work in a purely type safe and declarative manner.

Creating a class in your code base for example:

```kotlin
@Serializable
data class Test(val name : String = "") : AutoJsonResponse()

@AutoRouting()
@Resource("/test")
class BasicJsonGet: AutoGet<AutoJsonResponse> {

    @Transient
    override var render: () -> AutoResponse = { Test("Hello World") }

}
```

Will automatically create this Routing Code: 

```kotlin
public fun Application.autoRoute() {
  routing {
    get<BasicJsonGet> {
       call.respond(it.render.invoke() as Any, typeInfo = TypeInfo(Any::class))
    }
  }
}
```