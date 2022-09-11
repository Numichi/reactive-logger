package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.reactor.MDCContext
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.mono
import mu.KLogger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExperimentalCoroutinesApi
internal class CoroutineOtherTest {

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
    }

    @Test
    fun `should get MDC data from snapshot (KLogger)`() {
        val logger = CoroutineKLogger.getLogger(mockk<KLogger>(relaxed = true))

        val x = mono { logger.snapshot()?.get("foo") }
            .contextWrite {
                val mdc = MDC()
                mdc["foo"] = "bar"
                MDCContext.put(it, mdc)
            }

        StepVerifier.create(x)
            .expectNext("bar")
            .verifyComplete()
    }

    @Test
    fun `should get MDC data from snapshot (Logger)`() {
        val logger = CoroutineLogger.getLogger(mockk<KLogger>(relaxed = true))

        val x = mono { logger.snapshot()?.get("foo") }
            .contextWrite {
                val mdc = MDC()
                mdc["foo"] = "bar"
                MDCContext.put(it, mdc)
            }

        StepVerifier.create(x)
            .expectNext("bar")
            .verifyComplete()

    }

    @Test
    fun `snapshot and direct export matching`() {
        val logger = CoroutineLogger.getLogger(mockk<KLogger>(relaxed = true))

        val mdcContextKey = logger.mdcContextKey

        val mdc = MDC()
        mdc["foo"] = "bar"

        val x = mono { logger.snapshot() }
            .contextWrite { MDCContext.put(it, mdc) }

        val y = Mono.deferContextual { context ->
            var mdcData: MDC? = null
            try {
                val temp: MDCSnapshot? = context.getOrEmpty<Map<String, String>>(mdcContextKey)
                    .map { MDCSnapshot.of(it) }
                    .orElse(null)

                temp?.use {
                    mdcData = MDC(mdcContextKey, it.copyOfContextMap)
                }

                return@deferContextual Mono.justOrEmpty(mdcData)
            } catch (exception: Exception) {
                return@deferContextual Mono.error<MDC>(exception)
            }
        }.contextWrite { MDCContext.put(it, mdc) }

        StepVerifier.create(Mono.zip(x, y))
            .expectNextMatches { it.t1 == it.t2 }
            .verifyComplete()
    }
}