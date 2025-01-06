package io.github.bsautner

import io.github.bsautner.autorouter.annotations.autoRoute
import io.ktor.server.application.*
import io.ktor.server.resources.*

fun Application.configureRouting() {
    install(Resources)
    autoRoute()
}

