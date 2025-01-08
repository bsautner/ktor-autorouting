package io.github.bsautner

import io.ktor.server.application.*
import io.ktor.server.resources.*

fun Application.configureRouting() {
    install(Resources)
  //  autoRoute()
}

