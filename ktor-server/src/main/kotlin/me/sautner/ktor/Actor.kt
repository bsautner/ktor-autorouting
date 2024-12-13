package me.sautner.ktor

import com.sautner.autorouter.AutoGet
import com.sautner.autorouter.AutoJsonResponse
import com.sautner.autorouter.AutoResponse
import com.sautner.ksp.annotations.AutoRouting
import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Test(val name : String = "test ${System.currentTimeMillis()}") : AutoJsonResponse()

class Actor {

    @AutoRouting(Test::class)
    @Resource("/actor")
    class Actor1(val name: String? = "test")  : AutoGet<AutoJsonResponse> {
        @Transient override var render: () -> AutoResponse = { Test("new test render $name ${System.currentTimeMillis()}") }
    }
}

