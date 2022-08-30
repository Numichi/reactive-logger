package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.reactor.MDCContext
import io.github.numichi.reactive.logger.reactor.MDCSnapshot
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.test.runTest
import mu.KLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier

@ExperimentalCoroutinesApi
internal class CoroutineOtherTest {

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
    }

    @Test
    fun `configuration with chain`() {
        runTest {
            val mockKLogger: KLogger = mockk(relaxed = true)
            val loggerK = CoroutineKLogger.reactorBuilder()
                .setContextKey(ReactorContext)
                .setContextExtractive { coroutineContext[it]?.context }
                .setScheduler(Schedulers.boundedElastic())
                .setMDCContextKey("other-key")
                .setLogger(mockKLogger)
                .build()

            assertEquals("other-key", loggerK.mdcContextKey)
            assertSame(Schedulers.boundedElastic(), loggerK.scheduler)
            assertEquals(mockKLogger, loggerK.reactorLogger.logger)

            val mockLogger: Logger = mockk(relaxed = true)
            val loggerL = CoroutineLogger.reactorBuilder()
                .setContextKey(ReactorContext)
                .setContextExtractive { coroutineContext[it]?.context }
                .setScheduler(Schedulers.boundedElastic())
                .setMDCContextKey("other-key")
                .setLogger(mockLogger)
                .build()

            assertEquals("other-key", loggerL.mdcContextKey)
            assertSame(Schedulers.boundedElastic(), loggerL.scheduler)
            assertEquals(mockLogger, loggerL.logger)
        }
    }

    @Test
    fun `configuration with attributes`() {
        runTest {
            val mockKLogger: KLogger = mockk(relaxed = true)
            val kBuilder = CoroutineKLogger.reactorBuilder()
            kBuilder.contextKey = ReactorContext
            kBuilder.contextExtractive = { coroutineContext[it]?.context }
            kBuilder.scheduler = Schedulers.boundedElastic()
            kBuilder.mdcContextKey = "other-key"
            kBuilder.logger = mockKLogger
            val loggerK = kBuilder.build()

            assertSame(ReactorContext, kBuilder.contextKey)
            assertSame(Schedulers.boundedElastic(), kBuilder.scheduler)
            assertEquals("other-key", kBuilder.mdcContextKey)
            assertEquals(mockKLogger, kBuilder.logger)

            assertEquals("other-key", loggerK.mdcContextKey)
            assertSame(Schedulers.boundedElastic(), loggerK.scheduler)
            assertEquals(mockKLogger, loggerK.logger)

            val mockLogger: Logger = mockk(relaxed = true)
            val builder = CoroutineLogger.reactorBuilder()
            builder.contextKey = ReactorContext
            builder.contextExtractive = { coroutineContext[it]?.context }
            builder.scheduler = Schedulers.boundedElastic()
            builder.mdcContextKey = "other-key"
            builder.logger = mockLogger
            val loggerL = builder.build()

            assertSame(ReactorContext, builder.contextKey)
            assertSame(Schedulers.boundedElastic(), builder.scheduler)
            assertEquals("other-key", builder.mdcContextKey)
            assertEquals(mockLogger, builder.logger)

            assertEquals("other-key", loggerL.mdcContextKey)
            assertSame(Schedulers.boundedElastic(), loggerL.scheduler)
            assertEquals(mockLogger, loggerL.logger)
        }
    }

    @Test
    fun `should get MDC data from snapshot (KLogger)`() {
        val logger = CoroutineKLogger.reactorBuilder()
            .setLogger(mockk(relaxed = true))
            .build()

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
        val logger = CoroutineLogger.reactorBuilder()
            .setLogger(mockk(relaxed = true))
            .build()

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
        val logger = CoroutineLogger.reactorBuilder()
            .setLogger(mockk(relaxed = true))
            .build()

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