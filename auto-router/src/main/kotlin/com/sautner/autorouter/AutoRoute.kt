package com.sautner.autorouter

import io.ktor.server.routing.*
import kotlinx.html.A
import kotlinx.html.DIV
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

@Serializable
sealed interface AutoResponse

@Serializable
open class AutoJsonResponse : AutoResponse



//
//open class AutoGetJson(@Transient val render: () -> AutoJsonResponse = {AutoJsonResponse()} ) : AutoGet
//
