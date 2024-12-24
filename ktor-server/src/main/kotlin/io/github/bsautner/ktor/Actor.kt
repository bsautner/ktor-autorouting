package io.github.bsautner.ktor

import io.github.bsautner.autorouter.*
import io.github.bsautner.autorouter.annotations.AutoRouting
import io.ktor.resources.*
import kotlinx.html.BODY
import kotlinx.html.div
import kotlinx.html.p
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Test(val name : String = "") : AutoJsonResponse()

@Serializable
data class Test2(val name : String = "") : AutoJsonResponse()

@Serializable
data class TestPostBodyResponse(val message : String = "") : AutoJsonResponse()

@Serializable
data class TestPostBody(val name : String = "") : AutoPostBody()

fun BODY.homepage() {
    div {
        p {
            +"Hello World!"
        }
    }
}

sealed class Website<T> : AutoWeb<T> {

    @AutoRouting()
    @Resource("/")
    class HomePage : Website<BODY>() {

         @Transient override var render: (BODY) -> Unit =  { div -> div.homepage() }


    }

}


    @AutoRouting(TestPostBody::class)
    @Resource("/sensor")
    class Sensor : AutoPost<TestPostBody, TestPostBodyResponse> {
        @Transient
        override var process: (TestPostBody) -> TestPostBodyResponse = { r ->

            TestPostBodyResponse("I'm a post body response - i got ${r.name}")
        }
    }


sealed class Actor : AutoGet<AutoJsonResponse> {

    @AutoRouting(Test::class)
    @Resource("/actor")
    class Actor1()  : Actor() {
        @Transient override var render: () -> AutoResponse = { Test("new test render  ${System.currentTimeMillis()}") }

        @AutoRouting(Test2::class)
        @Resource("new")
        class NewActor(val parent : Actor1 = Actor1())  : Actor() {
            @Transient override var render: () -> AutoResponse = { Test2("actor 2 test ${System.currentTimeMillis()}") }
        }
    }


}

