package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.mockk.clearAllMocks
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import mu.KLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import reactor.test.StepVerifier
import reactor.util.context.Context

class ICoreTest {
    private val kLogger: KLogger = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)

    private val reactiveKLogger = ReactiveKLogger.getLogger(kLogger)
    private val reactiveLogger = ReactiveLogger.getLogger(logger)
    private val coroutineKLogger = CoroutineKLogger.getLogger(kLogger)
    private val coroutineLogger = CoroutineLogger.getLogger(logger)

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun takeMDCSnapshotTest() {
        val mdc1 = randomMap(1)
        val mdc2 = randomMap(1)
        val mdc3 = randomMap(1)
        val mdc4 = randomMap(1)
        val context1 = Context.of(Configuration.defaultReactorContextMdcKey, mdc1)
        val context2 = Context.of(Configuration.defaultReactorContextMdcKey, mdc2)
        val context3 = Context.of(Configuration.defaultReactorContextMdcKey, mdc3)
        val context4 = Context.of(Configuration.defaultReactorContextMdcKey, mdc4)

        reactiveKLogger.takeMDCSnapshot(context1).use {
            assertEquals(org.slf4j.MDC.getCopyOfContextMap(), mdc1)
        }

        reactiveLogger.takeMDCSnapshot(context2).use {
            assertEquals(org.slf4j.MDC.getCopyOfContextMap(), mdc2)
        }

        coroutineKLogger.takeMDCSnapshot(context3).use {
            assertEquals(org.slf4j.MDC.getCopyOfContextMap(), mdc3)
        }

        coroutineLogger.takeMDCSnapshot(context4).use {
            assertEquals(org.slf4j.MDC.getCopyOfContextMap(), mdc4)
        }
    }

    @Test
    fun readMDCTest() {
        val mdc = randomMap(1)
        val context = Context.of(Configuration.defaultReactorContextMdcKey, mdc)

        assertEquals(mdc, reactiveKLogger.readMDC(context))
        assertEquals(mdc, reactiveLogger.readMDC(context))
        assertEquals(mdc, coroutineKLogger.readMDC(context))
        assertEquals(mdc, coroutineLogger.readMDC(context))
    }

    @Test
    fun logConsumerTest() {
        run {
            val mono = Mono.just("test")
                .doOnEach(reactiveLogger.logConsumer { logger, value -> logger.info(value.get()) })

            StepVerifier.create(mono)
                .expectNext("test")
                .verifyComplete()

            verify(exactly = 1) { logger.info("test") }
        }

        clearMocks(logger)

        run {
            val mono = Mono.just("info")
                .flatMap { Mono.error<Exception>(Exception("error")) }
                .doOnEach(reactiveLogger.logConsumer { logger, signal ->
                    if (signal.type == SignalType.ON_ERROR) {
                        logger.info(signal.throwable?.message)
                    }
                })

            StepVerifier.create(mono)
                .expectError()
                .verify()

            verify(exactly = 1) { logger.info("error") }
        }
    }
}