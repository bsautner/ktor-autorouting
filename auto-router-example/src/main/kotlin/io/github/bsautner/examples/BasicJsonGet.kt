package io.github.bsautner.examples

import io.github.bsautner.autorouter.AutoGet
import io.github.bsautner.autorouter.AutoJsonResponse
import io.github.bsautner.autorouter.AutoResponse
import io.github.bsautner.autorouter.annotations.AutoRouting
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Test(val name : String = "") : AutoJsonResponse()

@AutoRouting()
@Resource("/test")
class BasicJsonGet: AutoGet<AutoJsonResponse> {

    @Transient
    override var render: () -> AutoResponse = { Test("Hello World") }

}