package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class ReactiveCoreTest {

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun snapshotTest() {
        val contextKey = "foobar"

        // without all
        run {
            val logger = ReactiveLogger.getLogger("")

            val mono = Mono.deferContextual { logger.snapshot(it) }

            StepVerifier.create(mono)
                .expectNext(MDC())
                .verifyComplete()
        }

        // with custom key and empty context
        run {
            val logger = ReactiveLogger.getLogger("", mdcContextKey = contextKey)

            val mono = Mono.deferContextual { logger.snapshot(it) }

            StepVerifier.create(mono)
                .expectNext(MDC(contextKey))
                .verifyComplete()
        }

        // with custom key and custom context
        run {
            val content = mapOf("A" to "B")
            val mdc = MDC(contextKey, content)
            val logger = ReactiveLogger.getLogger("", mdcContextKey = contextKey)

            val mono = Mono.deferContextual { logger.snapshot(it) }
                .contextWrite { it.put(contextKey, content) }

            StepVerifier.create(mono)
                .expectNext(mdc)
                .verifyComplete()
        }

        // with custom key and without parameter
        run {
            val content = mapOf("A" to "B")
            val mdc = MDC(contextKey, content)
            val logger = ReactiveLogger.getLogger("", mdcContextKey = contextKey)

            val mono = Mono.defer { logger.snapshot() }
                .contextWrite { it.put(contextKey, content) }

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
            val runnable = Runnable { throw Exception() }
            val logger = ReactiveKLogger.getLogger("")

            val mono = Mono.defer { logger.wrap(runnable) }

            StepVerifier.create(mono)
                .expectError(Exception::class.java)
                .verify()
        }
    }
}