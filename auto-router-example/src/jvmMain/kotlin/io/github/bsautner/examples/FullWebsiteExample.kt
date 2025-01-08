package io.github.bsautner.examples

import io.github.bsautner.autorouter.AutoJsonResponse
import io.github.bsautner.autorouter.AutoMultipartPost
import io.github.bsautner.autorouter.AutoPostBody
import io.github.bsautner.autorouter.AutoWeb
import io.github.bsautner.autorouter.annotations.AutoRouting
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


/**
 * AutoWeb responds with Kotlin HTML DSL classes that will be routed as HTML
 * https://kotlinlang.org/docs/typesafe-html-dsl.html
 */
@Resource("/")
class HomePage : AutoWeb<BODY> {

    @Transient
    override var render: (BODY) -> Unit = { body -> body.homepage() }
}

/**
 * You can implement multiple interfaces for handling GET, POST, DELETE and PUT actions on the same route.
 * AutoPost takes a post body and response body class you define.
 *
 * You must let auto router know the class to use for the post body.
 */
@AutoRouting(PostBody::class)
@Resource("/form")
class FormPage : AutoWeb<BODY>, AutoMultipartPost {

    @Transient
    override var render: (BODY) -> Unit = { body -> body.formpage() }

    @Transient
    override var process: (Parameters) -> Unit = {
        HttpStatusCode.OK

    }

}

@Serializable
data class PostBody(val name: String = "", val email: String = "") : AutoPostBody()

@Serializable
data class PostResponse(val message: String = "") : AutoJsonResponse()

fun BODY.homepage() {
    div {
        p {
            +"Auto Router Website Example"
        }
        ul {
            li { a(href = "/form") { +"Form Example" } }
        }
    }
}

fun BODY.formpage() {
    div {
        p {
            +"Auto Router Website Form Example"
        }
        form(action = "/form", method = FormMethod.post) {
            label {
                +"Name: "
                textInput(name = "name") {
                    placeholder = "Enter your name"
                }
            }
            br()
            label {
                +"Email: "
                emailInput(name = "email") {
                    placeholder = "Enter your email"
                }
            }

            br()
            submitInput {
                value = "Submit"
            }
        }
    }
}

