package com.sautner.autorouter

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


interface AutoGet<T> {

    var render: () -> AutoResponse

}
interface AutoPost {

}
interface AutoPut
interface AutoDelete
