package com.ramalwi.plant.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CacheOnSuccess<T : Any>(
    private val onErrorFallback: (suspend () -> T)? = null,
    private val block: suspend () -> T
) {
    private val mutex = Mutex()

    @Volatile
    private var deferred: Deferred<T>? = null

    suspend fun getOrAwait(): T {
        return supervisorScope {
            val currentDeferred = mutex.withLock {
                deferred?.let { return@withLock it }

                async {
                    block()
                }.also {
                    deferred = it
                }
            }
            currentDeferred.safeAwait()
        }
    }

    private suspend fun Deferred<T>.safeAwait(): T {
        try {
            return await()
        } catch (throwable: Throwable) {
            mutex.withLock {
                if (deferred == this) {
                    deferred = null
                }
            }

            if (throwable is CancellationException) {
                throw throwable
            }

            onErrorFallback?.let { fallback -> return fallback() }

            throw throwable
        }
    }

}