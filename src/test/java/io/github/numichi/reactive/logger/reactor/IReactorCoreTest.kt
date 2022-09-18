package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class IReactorCoreTest {

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun snapshotTest() {
        run {
            val logger = ReactiveLogger.getLogger("")
            val mdc = MDC(mapOf())

            val mono = Mono.deferContextual { logger.snapshot(it) }

            StepVerifier.create(mono)
                .expectNext(mdc)
                .verifyComplete()
        }

        run {
            val logger = ReactiveLogger.getLogger("", mdcContextKey = "foobar")
            val mdc = MDC("foobar", mapOf())

            val mono = Mono.deferContextual { logger.snapshot(it) }

            StepVerifier.create(mono)
                .expectNext(mdc)
                .verifyComplete()
        }

        run {
            val logger = ReactiveLogger.getLogger("", mdcContextKey = "foobar")
            val mdc = MDC("foobar", mapOf("A" to "B"))

            val mono = Mono.deferContextual { logger.snapshot(it) }
                .contextWrite { it.putAllMap(mapOf("foobar" to mapOf("A" to "B"))) }

            StepVerifier.create(mono)
                .expectNext(mdc)
                .verifyComplete()
        }

        run {
            val logger = ReactiveKLogger.getLogger("", mdcContextKey = "foobar")
            val mdc = MDC("foobar", mapOf("A" to "B"))

            val mono = Mono.deferContextual { logger.snapshot(it) }
                .contextWrite { it.putAllMap(mapOf("foobar" to mapOf("A" to "B"))) }

            StepVerifier.create(mono)
                .expectNext(mdc)
                .verifyComplete()
        }
    }

    @Test
    fun wrapTest() {
        run {
            val mock = mockk<Logger>(relaxed = true)
            val runnable = Runnable { mock.info("info") }
            val logger = ReactiveLogger.getLogger("")

            val mono = Mono.defer { logger.wrap(runnable) }

            StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete()

            verify(exactly = 1) { mock.info("info") }
        }

        run {
            val mock = mockk<Logger>(relaxed = true)
            val runnable = Runnable { mock.info("info") }
            val logger = ReactiveKLogger.getLogger("")

            val mono = Mono.defer { logger.wrap(runnable) }

            StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete()

            verify(exactly = 1) { mock.info("info") }
        }
    }
}