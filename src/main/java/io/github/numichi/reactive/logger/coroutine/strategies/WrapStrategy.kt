package io.github.numichi.reactive.logger.coroutine.strategies

import reactor.core.publisher.Mono

interface WrapStrategy<T> {
    fun getLogger(): T
    suspend fun <R> wrap(fn: (T) -> Mono<R>): R
}