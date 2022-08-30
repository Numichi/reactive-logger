package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class MDCHookContextTest {

    @BeforeEach
    fun setUp() {
        Configuration.reset()
    }

    @Test
    fun `should lift values from context into MDC by hook format`() {
        Configuration.addGenericHook<String>(contextKey = "after1", order = 0) { mapOf("mdcAfter1" to it!!.uppercase()) }
        Configuration.addGenericHook<Int>(contextKey = "after2", order = 0) { mapOf("mdcAfter2" to "${it!! * 100}") }
        Configuration.addGenericHook<String>(contextKey = "before1", order = -1) { mapOf("mdcBefore1" to it!!.uppercase()) }

        val reactiveContextMap = mapOf(
            "after1" to "aaa",
            "after2" to 11,
            "before1" to "bbb"
        )

        val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { it.putAllMap(reactiveContextMap) }

        StepVerifier.create(resultDefault)
            .expectNextMatches {
                it.size == 3
                && it["mdcAfter1"] == "AAA"
                && it["mdcAfter2"] == "1100"
                && it["mdcBefore1"] == "BBB"
            }
            .verifyComplete()
    }

    @Test
    fun `should be overwritten before hook with after hook`() {
        Configuration.addGenericHook<Int>(contextKey = "key", order = 0) { mapOf("hookKey" to "${it!! * 2}") }
        Configuration.addGenericHook<Int>(contextKey = "key", order = -1) { mapOf("hookKey" to "${it!! * 3}") }

        val reactiveContextMap = mapOf("key" to 1)

        val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { it.putAllMap(reactiveContextMap) }

        StepVerifier.create(resultDefault)
            .expectNextMatches { it.size == 1 && it["hookKey"] == "2" }
            .verifyComplete()
    }
}