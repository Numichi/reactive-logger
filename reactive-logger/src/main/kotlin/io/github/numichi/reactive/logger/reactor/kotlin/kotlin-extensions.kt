package io.github.numichi.reactive.logger.reactor.kotlin

import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun <T : Any> Flux<T>.logOnNext(logger: (T) -> Mono<*>): Flux<T> {
    return this.flatMap { logger(it).thenReturn(it) }
}

fun <T : Any> Mono<T>.logOnNext(logger: (T) -> Mono<*>): Mono<T> {
    return this.flatMap { logger(it).thenReturn(it) }
}

fun <T> Flux<T>.logOnNextCoroutine(logger: suspend (T) -> Unit): Flux<T> {
    return this.flatMap { value ->
        mono {
            logger(value)
            value
        }
    }
}

fun <T> Mono<T>.logOnNextCoroutine(logger: suspend (T) -> Unit): Mono<T> {
    return this.flatMap { value ->
        mono {
            logger(value)
            value
        }
    }
}

fun <T> Flux<T>.logOnError(logger: (Throwable) -> Mono<*>): Flux<T> {
    return this.onErrorResume { throwable ->
        logger(throwable)
            .then(Mono.error(throwable))
    }
}

fun <T> Mono<T>.logOnError(logger: (Throwable) -> Mono<*>): Mono<T> {
    return this.onErrorResume { throwable ->
        logger(throwable)
            .then(Mono.error(throwable))
    }
}

fun <T> Flux<T>.logOnErrorCoroutine(logger: suspend (Throwable) -> Unit): Flux<T> {
    return this.onErrorResume { throwable ->
        mono {
            logger(throwable)
            throw throwable
        }
    }
}

fun <T> Mono<T>.logOnErrorCoroutine(logger: suspend (Throwable) -> Unit): Mono<T> {
    return this.onErrorResume { throwable ->
        mono {
            logger(throwable)
            throw throwable
        }
    }
}
