package io.github.bsautner.autorouter

import io.ktor.http.*
import kotlin.reflect.KClass


interface AutoGet<T> {
    var render: () -> AutoResponse
}

interface AutoWeb<T> {
    var render: (T) -> Unit
}

interface AutoPost<T, R> {
    var process: (T) -> R
}

interface AutoMultipartPost {
    var process: (Parameters) -> Unit
}

inline fun <reified T, reified R> AutoPost<T, R>.getPostBodyClass() : KClass<*> {
    return T::class
}
inline fun <reified T, reified R> AutoPost<T, R>.getPostResponseBodyClass() : KClass<*> {
    return R::class
}


interface AutoPut
interface AutoDelete
