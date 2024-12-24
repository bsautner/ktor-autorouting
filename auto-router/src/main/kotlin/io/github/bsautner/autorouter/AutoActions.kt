package io.github.bsautner.autorouter

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

inline fun <reified T, reified R> AutoPost<T, R>.getPostBodyClass() : KClass<*> {
    return T::class
}
inline fun <reified T, reified R> AutoPost<T, R>.getPostResponseBodyClass() : KClass<*> {
    return R::class
}


interface AutoPut
interface AutoDelete
