package io.github.bsautner

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureTemplating()
    configureFrameworks()
    configureRouting()

}
