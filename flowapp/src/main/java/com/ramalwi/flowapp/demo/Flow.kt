package com.ramalwi.flowapp.demo

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take

fun makeFlow() = flow {
    println("sending first value")
    emit(1)
    println("first value collected, sending another value")
    emit(2)
    println("second value collected, sending a third value")
    emit(3)
    println("done")
}

suspend fun main() {

    collecting()
}


suspend fun collecting(){
    makeFlow().collect { value ->
        println("got $value")
    }
    println("flow is completed")
}

suspend fun take(){
    makeFlow().take(2).collect { value ->
        println("got $value")
    }
    println("flow is completed")

}