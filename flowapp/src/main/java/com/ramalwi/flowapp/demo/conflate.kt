package com.ramalwi.flowapp.demo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

/**
 * [Flow<T>.buffer()] flows emissions via channel of a specified capacity and runs collector in a separate coroutine.
   Collector gets every single emitted value.

 * [Flow<T>.conflate()] runs collector in a separate coroutine.
   Emitter is never suspended due to a slow collector.
   Collector always gets the most recent value emitted.
   Conflate does care about the last value only, so that collector will cancel the previous operation once a new value received.

 * [Flow<T>.conflate()] runs collector in a separate coroutine.
   Emitter is never suspended due to a slow collector.
   Collector always gets the most recent value emitted.
   Conflate does care about the last value only, so that collector will cancel the previous operation once a new value received.

 * Execution time = (producing time + collecting time) * capacity
    - Normal call   -> et = (producing time + collecting time) * capacity = (100 + 300) * 30 = 12000
    - Buffer call   -> et = [(0 + collecting time) * capacity] = 300 * 30 = 9000
    - Conflate call -> et = total collected * collecting time  = 12 * 300 = 3600
 */

val defaultDispatcher = Dispatchers.Default
val flow = flow {
    for (i in 1..30) {
        delay(100)
        emit(i)
    }
}.flowOn(defaultDispatcher)

suspend fun normalCall(){
    val time1 = measureTimeMillis {
        val withoutConflate = flow.onEach { delay(300) }.flowOn(defaultDispatcher).toList()
        println(withoutConflate)
    }
    println(time1)

    val time = measureTimeMillis {
        flow.flowOn(defaultDispatcher).collect {
            delay(300)
        }
    }
    println(time)
}

suspend fun bufferCall(){
    val time2 = measureTimeMillis {
        val withBuffer = flow.buffer().onEach { delay(300) }.flowOn(defaultDispatcher).toList()
        println(withBuffer)
    }
    println(time2)
}

suspend fun conflateCall(){

    val time3 = measureTimeMillis {
        val withConflate = flow.conflate().onEach { delay(300) }.flowOn(defaultDispatcher).toList()
        println(withConflate)
    }
    println(time3)
}


suspend fun collectLatestCall(){
    val time = measureTimeMillis {
        flow.flowOn(defaultDispatcher).collectLatest {
            delay(300)
        }
    }
    println(time)
}


suspend fun main() {
    normalCall()
    collectLatestCall()
}

