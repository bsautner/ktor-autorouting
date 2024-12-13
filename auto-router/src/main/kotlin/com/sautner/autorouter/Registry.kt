package com.sautner.autorouter

object Registry {

    val map = mutableMapOf<String, () -> AutoJsonResponse>()
    fun registerRoute(key: String, handler: () -> AutoJsonResponse) {
        map[key] = handler
    }


}