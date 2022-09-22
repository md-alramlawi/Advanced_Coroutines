package com.ramalwi.plants.demo

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
    Run 2 coroutines together to increment the count by 1000 for each one
 */

private val mutex = Mutex()
private var counter = 0


suspend fun main( ) {
    runWithoutMutex()
    runWithMutex()
}

suspend fun runWithoutMutex( ) {
    counter = 0
    val job1NoMutex = CoroutineScope(Default).launch {
        incrementCounterByThousand()
    }

    val job2NoMutex = CoroutineScope(Default).launch {
        incrementCounterByThousand()
    }

    joinAll(job1NoMutex, job2NoMutex)

    println("without mutex : $counter")
}

suspend fun runWithMutex( ) {
    counter = 0
    val job3WithMutex = CoroutineScope(Default).launch {
        mutex.withLock {
            incrementCounterByThousand()
        }
    }

    val job4WithMutex = CoroutineScope(Default).launch {
        mutex.withLock {
            incrementCounterByThousand()
        }
    }


    joinAll(job3WithMutex, job4WithMutex)

    println("with mutex    : $counter")
}

private fun incrementCounterByThousand() {
    for (i in 0 until 1000) {
        counter++
    }
}