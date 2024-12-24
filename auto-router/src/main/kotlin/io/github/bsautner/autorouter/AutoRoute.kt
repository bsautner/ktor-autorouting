package io.github.bsautner.autorouter

import kotlinx.serialization.Serializable

@Serializable
sealed interface AutoResponse

@Serializable
open class AutoJsonResponse : AutoResponse

@Serializable
open class AutoHtmlResponse : AutoResponse

@Serializable
open class AutoPostBody
