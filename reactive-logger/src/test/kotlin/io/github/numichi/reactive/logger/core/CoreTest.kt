package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.LoggerFactory
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import mu.KLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.MDC
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import reactor.test.StepVerifier
import reactor.util.context.Context
import java.util.*

internal class CoreTest {
    private val contextKey = UUID.randomUUID().toString()
    private val logger: Logger = mockk(relaxed = true)
    private val kLogger: KLogger = LoggerFactory.getKLogger(logger)
    private val reactiveLogger = ReactiveLogger.getLogger(logger, contextKey, null)
    private val reactiveKLogger = ReactiveKLogger.getLogger(kLogger)
    private val coroutineLogger = CoroutineLogger.getLogger(logger)
    private val coroutineKLogger = CoroutineKLogger.getLogger(kLogger)

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun getLoggerTest() {
        assertEquals(logger, reactiveLogger.logger)
        assertEquals(logger, reactiveKLogger.logger.underlyingLogger)
        assertEquals(kLogger, reactiveKLogger.logger)

        assertEquals(logger, coroutineLogger.logger)
        assertEquals(logger, coroutineKLogger.logger.underlyingLogger)
        assertEquals(kLogger, coroutineKLogger.logger)
    }

    @Test
    fun wrapRunnerTest() {
        val randomKey = UUID.randomUUID().toString()
        val randomValue = UUID.randomUUID().toString()
        val mdc = mapOf(randomKey to randomValue)
        val contextContext = mapOf<Any, Any>(
            UUID.randomUUID() to UUID.randomUUID(),
            contextKey to mdc
        )
        val context = Context.of(contextContext)

        reactiveLogger.wrapRunner(context) {
            assertEquals(mdc, MDC.getCopyOfContextMap())
        }
    }

    @Test
    fun logOnEachNextTest() {
        val mono = Mono.just("test")
            .doOnEach(reactiveLogger.logOnEach { logger, value -> logger.info(value.get()) })

        StepVerifier.create(mono)
            .expectNext("test")
            .verifyComplete()

        verify(exactly = 1) { logger.info("test") }
    }

    @Test
    fun logOnEachErrorTest() {
        val mono = Mono.defer { Mono.error<Exception>(Exception("error")) }
            .doOnEach(reactiveLogger.logOnEach { logger, signal ->
                if (signal.type == SignalType.ON_ERROR) {
                    logger.info(signal.throwable?.message)
                }
            })

        StepVerifier.create(mono)
            .expectError()
            .verify()

        verify(exactly = 1) { logger.info("error") }
    }

    //endregion
}