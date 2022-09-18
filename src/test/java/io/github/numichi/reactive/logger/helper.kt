package io.github.numichi.reactive.logger

import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.ContextView
import java.util.*

fun randomText(): String {
    return UUID.randomUUID().toString()
}

fun randomMap(i: Int): Map<String, String> {
    var index = i
    val expected: MutableMap<String, String> = HashMap()
    while (0 < index) {
        expected[randomText()] = randomText()
        index--
    }
    return expected
}

fun step(logger: () -> Mono<ContextView>) {
    StepVerifier.create(logger()).expectNextCount(1).verifyComplete()
}