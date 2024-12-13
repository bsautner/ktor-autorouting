package me.sautner.ktor

import com.sautner.autorouter.AutoJsonResponse
import com.sautner.autorouter.Registry
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val actorSerializersModule = SerializersModule {
        polymorphic(AutoJsonResponse::class) {
            subclass(Test::class, Test.serializer())

        }

    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                encodeDefaults = true
                isLenient = true
                serializersModule = actorSerializersModule
            }
        )
    }

    install(Resources)


    routing {
        get<Actor.Actor1> {
             call.respond(it.render.invoke() as Test, typeInfo = TypeInfo(Test::class))
        }
    }
    printRoutes()

}
private fun Application.printRoutes() {

    this.routing {}.run {
        getAllRoutes().forEach {
            println("CG: Route: $it")
        }
    }
}
