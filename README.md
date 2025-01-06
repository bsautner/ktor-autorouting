[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.bsautner/auto-router)](https://central.sonatype.com/artifact/io.github.bsautner/auto-router/overview)

# ktor-autorouting

Auto Router will scan your code for usages of @Resource from [typesafe routing in ktor](https://ktor.io/docs/server-resources.html) and generate your routing pluging code for you using with [KSP Code Generation](https://kotlinlang.org/docs/ksp-overview.html).

The library provides interfaces and annotations you can use with your Resource to process your incoming and outgoing data with lambdas.

Your Ktor Server routing plugin usage will always be this simple:

```kotlin
fun Application.module() {
    install(Resources)
    autoRoute() //autoroute is an Application extension function re-generated with each build 
}
```
You can then work in a purely type safe and declarative manner.

# Example

You can see fully implemented examples of all types of routing [here](https://github.com/bsautner/ktor-autorouting/tree/main/auto-router-example).

```kotlin

//create a response object for GET. 
@Serializable
data class Test(val name : String = "") : AutoJsonResponse()

// GET requests to your server /test path will be routed the render lambda.
@Resource("/test")
class BasicJsonGet: AutoGet<AutoJsonResponse> {
    @Transient
    override var render: () -> AutoResponse = { Test("Hello World!") }
}
```

Autorouter Will automatically create this Routing Code:

```kotlin
public fun Application.autoRoute() {
  routing {
    get<BasicJsonGet> {
       call.respond(it.render.invoke() as Any, typeInfo = TypeInfo(Any::class))
    }
  }
}
```

Your routing code will always be clean, organized and type safe with perfect polymorphic serialization done for you.

## Setup

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
