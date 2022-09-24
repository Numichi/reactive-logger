package io.github.numichi.reactive.logger.core

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.coroutine.withMDCContext
import io.github.numichi.reactive.logger.exceptions.NotEmittedValueException
import io.github.numichi.reactive.logger.reactor.MDCContext
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import mu.KLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context

@ExperimentalCoroutinesApi
class CoroutineCoreTest {

    @BeforeEach
    fun afterEach() {
        Configuration.reset()
    }

    @Test
    fun logSignalTest() {
        val logger: Logger = mockk(relaxed = true)
        val coroutineLogger = CoroutineLogger.getLogger(logger)

        run {
            val mono = Mono.just("test")
                .doOnEach { s ->
                    if (s.isOnNext) {
                        coroutineLogger.logSignal(s) { it.info(s.get()) }
                    }
                }

            StepVerifier.create(mono)
                .expectNext("test")
                .verifyComplete()

            verify(exactly = 1) { logger.info("test") }

        }

        clearMocks(logger)

        run {
            val mono = Mono.error<Exception>(Exception("error"))
                .doOnEach { s ->
                    if (s.isOnError) {
                        coroutineLogger.logSignal(s) { it.info(s.throwable?.message) }
                    }
                }

            StepVerifier.create(mono)
                .expectError()
                .verify()

            verify(exactly = 1) { logger.info("error") }
        }
    }

    // TODO: Remove on v3.3.0
    @Test
    fun getDeprecatedContextResolver() {
        val logger: Logger = mockk(relaxed = true)
        val coroutineLogger = CoroutineLogger.getLogger(logger)

        runTest {
            assertEquals(null, coroutineLogger.contextResolver())
        }

        runTest {
            val context = Context.of("A", "B")
            withContext(context.asCoroutineContext()) {
                assertEquals(context, coroutineLogger.contextResolver())
            }
        }
    }

    // TODO: Remove on v3.3.0
    @Test
    fun getDeprecatedReactorLogger() {
        val logger: Logger = mockk(relaxed = true)
        val coroutineLogger = CoroutineLogger.getLogger(logger)
        val coroutineKLogger = CoroutineKLogger.getLogger(logger)

        assertEquals(coroutineLogger.reactiveLogger, coroutineLogger.reactorLogger)
        assertEquals(coroutineKLogger.reactiveLogger, coroutineKLogger.reactorLogger)
    }

    @Test
    fun getSnapshotTest() {
        val logger: Logger = mockk(relaxed = true)
        val coroutineLogger1 = CoroutineLogger.getLogger(logger)
        val coroutineLogger2 = CoroutineLogger.getLogger(logger, contextKey = "foo")
        val coroutineKLogger1 = CoroutineKLogger.getLogger(logger)
        val coroutineKLogger2 = CoroutineKLogger.getLogger(logger, contextKey = "foo")

        runTest {
            val mdc = readMdc()
            mdc["Foo"] = "Bar"
            withMDCContext(mdc) {
                val snapshot1 = coroutineLogger1.snapshot()
                val snapshot2 = coroutineKLogger1.snapshot()
                assertEquals(snapshot1, snapshot2)
                assertEquals(mdc, snapshot2)
            }
        }

        runTest {
            val snapshot1 = coroutineLogger1.snapshot()
            val snapshot2 = coroutineKLogger1.snapshot() // not null
            assertEquals(snapshot1, snapshot2)
            assertEquals(MDC(), snapshot2)
        }

        runTest {
            val mdc = readMdc("foo")
            mdc["Foo"] = "Bar"
            withMDCContext(mdc) {
                val snapshot1 = coroutineLogger2.snapshot()
                val snapshot2 = coroutineKLogger2.snapshot()
                assertEquals(snapshot1, snapshot2)
                assertEquals(mdc, snapshot2)
            }
        }

        runTest {
            val mdc = MDC("foo", mapOf("bar" to "baz"))
            val ctx = Context.of(mapOf("foo" to mapOf("bar" to "baz")))
            val snapshot1 = coroutineLogger2.snapshot(ctx)
            val snapshot2 = coroutineLogger2.snapshot(ctx)
            assertEquals(snapshot1, snapshot2)
            assertEquals(mdc, snapshot2)
        }
    }

    @Test
    fun `should get MDC data from snapshot (KLogger)`() {
        val logger = CoroutineKLogger.getLogger(mockk<KLogger>(relaxed = true))

        val x = mono { logger.snapshot()["foo"] }
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

        val x = mono { logger.snapshot()["foo"] }
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
    fun wrapTest() {
        // without MDC
        runTest {
            val log = mockk<Logger>(relaxed = true)
            val logger = CoroutineLogger.getLogger(log)
            logger.wrap { it.info("message") }
            verify(exactly = 1) { log.info("message") }
        }

        // with MDC
        runTest {
            val mdc = readMdc()
            mdc["Foo"] = "Bar"
            withMDCContext(mdc) {
                val log = mockk<Logger>(relaxed = true)
                val logger = CoroutineLogger.getLogger(log)
                logger.wrap { it.info("message") }
                verify(exactly = 1) { log.info("message") }
            }
        }

        // NotEmittedValueException
        runTest {
            val mdc = readMdc()
            mdc["Foo"] = "Bar"
            withMDCContext(mdc) {
                val log = mockk<Logger>(relaxed = true)
                val logger = CoroutineLogger.getLogger(log)
                val error = assertThrows<NotEmittedValueException> {
                    logger.wrap<Any> { Mono.empty() }
                }

                assertEquals("Did not emit any value", error.message)
                assertSame(NoSuchElementException::class.java, error.cause?.javaClass)
            }
        }

        // throw IllegalArgumentException or custom exception
        runTest {
            val log = mockk<Logger>(relaxed = true)
            val logger = CoroutineLogger.getLogger(log)

            assertThrows<IllegalArgumentException> {
                logger.wrap<Any> { Mono.error(IllegalArgumentException()) }
            }

            assertThrows<IllegalArgumentException> {
                logger.wrap<Any> { throw IllegalArgumentException() }
            }
        }
    }

    @Test
    fun wrapResultTest() {
        val log = mockk<Logger>()
        val logger = CoroutineLogger.getLogger(log)

        assertThrows<UnsupportedOperationException> {
            runTest {
                logger.wrapResult { it.info("") }
                Unit
            }
        }
    }
}