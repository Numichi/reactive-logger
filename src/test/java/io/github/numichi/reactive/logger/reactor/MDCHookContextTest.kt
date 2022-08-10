package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.models.MDC
import io.github.numichi.reactive.logger.models.MDCHook
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class MDCHookContextTest {

    @BeforeEach
    fun setUp() {
        DefaultValues.getInstance().reset()
    }

    @Test
    fun `should lift values from context into MDC by hook format`() {
        val customHook1 = MDCHook<String>(contextKey = "after1", hook = { mapOf("mdcAfter1" to it.uppercase()) }, order = 0)
        val customHook2 = MDCHook<Int>(contextKey = "after2", hook = { mapOf("mdcAfter2" to "${it * 100}") }, order = 0)
        val customHook3 = MDCHook<String>(contextKey = "before1", hook = { mapOf("mdcBefore1" to it.uppercase()) }, order = -1)
        DefaultValues.getInstance().addHook(customHook1)
        DefaultValues.getInstance().addHook(customHook2)
        DefaultValues.getInstance().addHook(customHook3)

        val reactiveContextMap = mapOf(
            "after1" to "aaa",
            "after2" to 11,
            "before1" to "bbb"
        )

        val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { it.putAllMap(reactiveContextMap) }

        StepVerifier.create(resultDefault)
            .expectNextMatches { it.size == 3 && it["mdcAfter1"] == "AAA" && it["mdcAfter2"] == "1100" && it["mdcBefore1"] == "BBB" }
            .verifyComplete()
    }

    @Test
    fun `should be overwritten before hook with after hook`() {
        val customHook1 = MDCHook<Int>(contextKey = "key", hook = { mapOf("hookKey" to "${it * 2}") }, order = 0)
        val customHook3 = MDCHook<Int>(contextKey = "key", hook = { mapOf("hookKey" to "${it * 3}") }, order = -1)
        DefaultValues.getInstance().addHook(customHook1)
        DefaultValues.getInstance().addHook(customHook3)

        val reactiveContextMap = mapOf("key" to 1)

        val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
            .contextWrite { it.putAllMap(reactiveContextMap) }

        StepVerifier.create(resultDefault)
            .expectNextMatches { it.size == 1 && it["hookKey"] == "2" }
            .verifyComplete()
    }
}