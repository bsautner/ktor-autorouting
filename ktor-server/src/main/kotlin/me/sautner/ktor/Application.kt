package me.sautner.ktor

import com.sautner.autorouter.printRoutes
import com.sautner.ktor.autoRoute
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import kotlinx.serialization.json.Json


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {


    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                encodeDefaults = true
                isLenient = true

            }
        )
    }

    install(Resources)
    autoRoute()

//    routing {
//        post<Sensor> {
//            val body = call.receive(it.getPostBodyClass())
//            val response = it.process(body as TestPostBody)
//            call.respond(response, TypeInfo(it.getPostResponseBodyClass()))
//        }
//    }

    printRoutes()

}

