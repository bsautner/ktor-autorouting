package io.github.bsautner.autorouter.annotations

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoRouting(val serializableResponse: KClass<*> = Any::class)
