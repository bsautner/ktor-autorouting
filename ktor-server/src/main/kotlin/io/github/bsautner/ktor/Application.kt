package io.github.bsautner.ktor

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

//
//install(ContentNegotiation) {
//    json(
//        Json {
//            prettyPrint = true
//            encodeDefaults = true
//            isLenient = true
//
//        }
//    )
//}
