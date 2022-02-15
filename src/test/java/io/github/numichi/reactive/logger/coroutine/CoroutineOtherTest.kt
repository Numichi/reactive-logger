package io.github.numichi.reactive.logger.coroutine

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.test.runTest
import mu.KLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import reactor.core.scheduler.Schedulers

@ExperimentalCoroutinesApi
internal class CoroutineOtherTest {


    @Test
    fun `test11`() = runTest {
        val mockKLogger: KLogger = mockk(relaxed = true)
        val loggerK = CoroutineKLogger.reactorBuilder()
            .withContextKey(ReactorContext)
            .withContextExtractive { coroutineContext[it]?.context }
            .withScheduler(Schedulers.boundedElastic())
            .withEnableError(false)
            .withMDCContextKey("other-key")
            .withLogger(mockKLogger)
            .build()

        assertEquals("other-key", loggerK.mdcContextKey)
        assertEquals(false, loggerK.isEnableError)
        assertSame(Schedulers.boundedElastic(), loggerK.scheduler)
        assertEquals(mockKLogger, loggerK.reactorLogger.logger)

        val mockLogger: Logger = mockk(relaxed = true)
        val loggerL = CoroutineLogger.reactorBuilder()
            .withContextKey(ReactorContext)
            .withContextExtractive { coroutineContext[it]?.context }
            .withScheduler(Schedulers.boundedElastic())
            .withEnableError(false)
            .withMDCContextKey("other-key")
            .withLogger(mockLogger)
            .build()

        assertEquals("other-key", loggerL.mdcContextKey)
        assertEquals(false, loggerL.isEnableError)
        assertSame(Schedulers.boundedElastic(), loggerL.scheduler)
        assertEquals(mockLogger, loggerL.logger)
    }

    @Test
    fun `test12`() = runTest {
        val mockKLogger: KLogger = mockk(relaxed = true)
        val kBuilder = CoroutineKLogger.reactorBuilder()
        kBuilder.contextKey = ReactorContext
        kBuilder.contextExtractive = { coroutineContext[it]?.context }
        kBuilder.scheduler = Schedulers.boundedElastic()
        kBuilder.enableError = false
        kBuilder.mdcContextKey = "other-key"
        kBuilder.logger = mockKLogger
        val loggerK = kBuilder.build()

        assertSame(ReactorContext, kBuilder.contextKey)
        assertSame(Schedulers.boundedElastic(), kBuilder.scheduler)
        assertEquals(false, kBuilder.enableError)
        assertEquals("other-key", kBuilder.mdcContextKey)
        assertEquals(mockKLogger, kBuilder.logger)

        assertEquals("other-key", loggerK.mdcContextKey)
        assertEquals(false, loggerK.isEnableError)
        assertSame(Schedulers.boundedElastic(), loggerK.scheduler)
        assertEquals(mockKLogger, loggerK.logger)

        val mockLogger: Logger = mockk(relaxed = true)
        val builder = CoroutineLogger.reactorBuilder()
        builder.contextKey = ReactorContext
        builder.contextExtractive = { coroutineContext[it]?.context }
        builder.scheduler = Schedulers.boundedElastic()
        builder.enableError = false
        builder.mdcContextKey = "other-key"
        builder.logger = mockLogger
        val loggerL = builder.build()

        assertSame(ReactorContext, builder.contextKey)
        assertSame(Schedulers.boundedElastic(), builder.scheduler)
        assertEquals(false, builder.enableError)
        assertEquals("other-key", builder.mdcContextKey)
        assertEquals(mockLogger, builder.logger)

        assertEquals("other-key", loggerL.mdcContextKey)
        assertEquals(false, loggerL.isEnableError)
        assertSame(Schedulers.boundedElastic(), loggerL.scheduler)
        assertEquals(mockLogger, loggerL.logger)
    }
}