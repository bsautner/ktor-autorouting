package com.sautner.ksp.annotations

import kotlin.reflect.KClass


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoRouting(val kClass: KClass<*>)
