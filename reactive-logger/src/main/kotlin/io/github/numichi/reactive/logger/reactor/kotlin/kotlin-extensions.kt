package io.github.numichi.reactive.logger.reactor.kotlin

import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

inline fun <reified T : Any> Flux<T>.logOnNext(crossinline logger: (T) -> Mono<*>): Flux<T> {
    return this.flatMap { logger(it).thenReturn(it) }
}

inline fun <reified T : Any> Mono<T>.logOnNext(crossinline logger: (T) -> Mono<*>): Mono<T> {
    return this.flatMap { logger(it).thenReturn(it) }
}

inline fun <reified T : Any> Flux<T>.logOnNextCoroutine(crossinline logger: suspend (T) -> Unit): Flux<T> {
    return this.flatMap { value ->
        mono {
            logger(value)
            value
        }
    }
}

inline fun <reified T : Any> Mono<T>.logOnNextCoroutine(crossinline logger: suspend (T) -> Unit): Mono<T> {
    return this.flatMap { value ->
        mono {
            logger(value)
            value
        }
    }
}

inline fun <T : Any> Flux<T>.logOnError(crossinline logger: (Throwable) -> Mono<*>): Flux<T> {
    return this.onErrorResume { throwable ->
        logger(throwable)
            .then(Mono.error(throwable))
    }
}

inline fun <reified T : Any> Mono<T>.logOnError(crossinline logger: (Throwable) -> Mono<*>): Mono<T> {
    return this.onErrorResume { throwable ->
        logger(throwable)
            .then(Mono.error(throwable))
    }
}

inline fun <reified T : Any> Flux<T>.logOnErrorCoroutine(crossinline logger: suspend (Throwable) -> Unit): Flux<T> {
    return this.onErrorResume { throwable ->
        mono {
            logger(throwable)
            throw throwable
        }
    }
}

inline fun <reified T : Any> Mono<T>.logOnErrorCoroutine(crossinline logger: suspend (Throwable) -> Unit): Mono<T> {
    return this.onErrorResume { throwable ->
        mono {
            logger(throwable)
            throw throwable
        }
    }
}
