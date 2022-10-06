package io.github.numichi.reactive.logger

import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

fun randomText(): String {
    return UUID.randomUUID().toString()
}

fun stepVerifierEmpty(logger: () -> Mono<*>) {
    StepVerifier.create(logger()).verifyComplete()
}

fun <T : Any> stepVerifierError(expect: Class<out Throwable>, logger: () -> Mono<T>) {
    StepVerifier.create(logger()).verifyError(expect)
}

fun <T : Any> stepVerifier(expect: T, logger: () -> Mono<T>) {
    StepVerifier.create(logger()).expectNext(expect).verifyComplete()
}