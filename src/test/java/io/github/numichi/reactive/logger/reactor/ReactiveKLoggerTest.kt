package io.github.numichi.reactive.logger.reactor

import org.slf4j.MDC as Slf4jMDC
import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.MDCContextTest
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger.Companion.builder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KLogger
import mu.toKLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import reactor.util.annotation.NonNull
import reactor.util.context.Context
import reactor.util.context.ContextView
import java.util.*

@ExperimentalCoroutinesApi
internal class ReactiveKLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = builder().setLogger(imperativeLogger).build()
    private val loggerWithError = builder().setLogger(imperativeLogger).build()

    companion object {
        fun randomText(): String {
            return UUID.randomUUID().toString()
        }

        @NonNull
        fun randomMap(i: Int): Map<String, String> {
            var index = i
            val expected: MutableMap<String, String> = HashMap()
            while (0 < index) {
                expected[randomText()] = randomText()
                index--
            }
            return expected
        }

        fun step(logger: () -> Mono<ContextView>) {
            StepVerifier.create(logger()).expectNextCount(1).verifyComplete()
        }
    }

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun builderWithClassLogger() {
        assertNotNull(builder().setLogger(LoggerFactory.getLogger(this.javaClass).toKLogger()).build())
    }

    @Test
    fun nameTest() {
        val name = randomText()
        every { imperativeLogger.name } returns name

        assertEquals(name, logger.name)
    }

    @Test
    fun readMDC() {
        val mdc = randomMap(1)
        val context = Context.of(Configuration.defaultReactorContextMdcKey, mdc)

        assertEquals(mdc, logger.readMDC(context))
    }

    @Test
    fun takeMDCSnapshot() {
        val mdc = randomMap(1)
        val context = Context.of(Configuration.defaultReactorContextMdcKey, mdc)

        logger.takeMDCSnapshot(context).use {
            assertEquals(Slf4jMDC.getCopyOfContextMap(), mdc)
        }
    }

    @Test
    fun imperative() {
        assertSame(logger.logger, imperativeLogger)
        assertSame(loggerWithError.logger, imperativeLogger)
    }

    @Test
    fun snapshot() {
        val mdc = MDC()
        mdc[randomText()] = randomText()

        var context1 = Context.empty()
        context1 = context1.put(Configuration.defaultReactorContextMdcKey, mdc)
        val snapshot1: Mono<MDC> = logger.snapshot(context1)
        StepVerifier.create(snapshot1)
            .expectNextMatches { mdc1 -> mdc1 == mdc }
            .verifyComplete()

        var context2 = Context.empty()
        context2 = context2.put(MDCContextTest.ANOTHER_CONTEXT_KEY, mdc)
        val snapshot2: Mono<MDC> = logger.snapshot(context2)
        StepVerifier.create(snapshot2)
            .expectNextMatches { mdc1 -> mdc1.size == 0 }
            .verifyComplete()


        val snapshot3: Mono<MDC> = loggerWithError.snapshot(context2)
        StepVerifier.create(snapshot3)
            .expectNext(MDC(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            .verifyComplete()
    }

    @Test
    fun contextKey() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = builder().setMDCContextKey(contextKey).build()
        assertSame(loggerWithCustomScheduler.mdcContextKey, contextKey)

        assertThrows<IllegalStateException> {
            builder().setMDCContextKey("").build()
        }

        assertThrows<IllegalStateException> {
            builder().setMDCContextKey(" ").build()
        }
    }

    @Test
    fun scheduler() {
        val customScheduler = Schedulers.newBoundedElastic(10, 10, randomText())
        val loggerWithCustomScheduler = builder().setScheduler(customScheduler).build()
        assertSame(loggerWithCustomScheduler.scheduler, customScheduler)
    }

    //region Trace
    @Test
    fun traceEnabled() {
        every { imperativeLogger.isTraceEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled, "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
    }

    @Test
    fun traceEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isTraceEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled(marker), "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
    }

    @Test
    fun traceMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(supplierCaptor)) } returns Unit
        step { logger.trace { message } }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.trace(exception) { message } }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.trace(marker) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.trace(marker, exception) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessage() {
        val message: String = randomText()
        step { logger.trace(message) }
        verify { imperativeLogger.trace(message) }
    }

    @Test
    fun traceFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.trace(format, argument1) }
        verify { imperativeLogger.trace(format, argument1) }
    }

    @Test
    fun traceFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.trace(format, argument1, argument2) }
        verify { imperativeLogger.trace(format, argument1, argument2) }
    }

    @Test
    fun traceFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.trace(format, argument1, argument2, argument3) }
        verify { imperativeLogger.trace(format, argument1, argument2, argument3) }
    }

    @Test
    fun traceMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.trace(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.trace(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun traceMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        step { logger.trace(marker, message) }
        verify { imperativeLogger.trace(marker, message) }
    }

    @Test
    fun traceFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.trace(marker, format, argument1) }
        verify { imperativeLogger.trace(marker, format, argument1) }
    }

    @Test
    fun traceFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.trace(marker, format, argument1, argument2) }
        verify { imperativeLogger.trace(marker, format, argument1, argument2) }
    }

    @Test
    fun traceFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.trace(marker, format, argument1, argument2, argument3) }
        verify { imperativeLogger.trace(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun traceMessageThrowableMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.trace(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Debug
    @Test
    fun debugEnabled() {
        every { imperativeLogger.isDebugEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
        assertFalse(logger.isDebugEnabled, "debug enabled when it should not be")
        assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
    }

    @Test
    fun debugEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isDebugEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
        assertFalse(logger.isDebugEnabled(marker), "debug enabled when it should not be")
        assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
    }

    @Test
    fun debugMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(supplierCaptor)) } returns Unit
        step { logger.debug { message } }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.debug(exception) { message } }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.debug(marker) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.debug(marker, exception) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessage() {
        val message: String = randomText()
        step { logger.debug(message) }
        verify { imperativeLogger.debug(message) }
    }

    @Test
    fun debugFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.debug(format, argument1) }
        verify { imperativeLogger.debug(format, argument1) }
    }

    @Test
    fun debugFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.debug(format, argument1, argument2) }
        verify { imperativeLogger.debug(format, argument1, argument2) }
    }

    @Test
    fun debugFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.debug(format, argument1, argument2, argument3) }
        verify { imperativeLogger.debug(format, argument1, argument2, argument3) }
    }

    @Test
    fun debugMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.debug(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.debug(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun debugMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        step { logger.debug(marker, message) }
        verify { imperativeLogger.debug(marker, message) }
    }

    @Test
    fun debugFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.debug(marker, format, argument1) }
        verify { imperativeLogger.debug(marker, format, argument1) }
    }

    @Test
    fun debugFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.debug(marker, format, argument1, argument2) }
        verify { imperativeLogger.debug(marker, format, argument1, argument2) }
    }

    @Test
    fun debugFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.debug(marker, format, argument1, argument2, argument3) }
        verify { imperativeLogger.debug(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun debugMessageThrowableMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.debug(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Info
    @Test
    fun infoEnabled() {
        every { imperativeLogger.isInfoEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
        assertFalse(logger.isInfoEnabled, "info enabled when it should not be")
        assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
    }

    @Test
    fun infoEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isInfoEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
        assertFalse(logger.isInfoEnabled(marker), "info enabled when it should not be")
        assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
    }

    @Test
    fun infoMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(supplierCaptor)) } returns Unit
        step { logger.info { message } }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.info(exception) { message } }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.info(marker) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.info(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.info(marker, exception) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessage() {
        val message: String = randomText()
        step { logger.info(message) }
        verify { imperativeLogger.info(message) }
    }

    @Test
    fun infoFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.info(format, argument1) }
        verify { imperativeLogger.info(format, argument1) }
    }

    @Test
    fun infoFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.info(format, argument1, argument2) }
        verify { imperativeLogger.info(format, argument1, argument2) }
    }

    @Test
    fun infoFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.info(format, argument1, argument2, argument3) }
        verify { imperativeLogger.info(format, argument1, argument2, argument3) }
    }

    @Test
    fun infoMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.info(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.info(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun infoMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        step { logger.info(marker, message) }
        verify { imperativeLogger.info(marker, message) }
    }

    @Test
    fun infoFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.info(marker, format, argument1, argument2) }
        verify { imperativeLogger.info(marker, format, argument1, argument2) }
    }

    @Test
    fun infoFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.info(marker, format, argument1) }
        verify { imperativeLogger.info(marker, format, argument1) }
    }

    @Test
    fun infoFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.info(marker, format, argument1, argument2, argument3) }
        verify { imperativeLogger.info(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun infoMessageThrowableMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.info(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.info(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Warn
    @Test
    fun warnEnabled() {
        every { imperativeLogger.isWarnEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
        assertFalse(logger.isWarnEnabled, "warn enabled when it should not be")
        assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
    }

    @Test
    fun warnEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isWarnEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
        assertFalse(logger.isWarnEnabled(marker), "warn enabled when it should not be")
        assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
    }

    @Test
    fun warnMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(supplierCaptor)) } returns Unit
        step { logger.warn { message } }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.warn(exception) { message } }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.warn(marker) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.warn(marker, exception) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessage() {
        val message: String = randomText()
        StepVerifier.create(logger.warn(message)).expectNextCount(1).verifyComplete()
        verify { imperativeLogger.warn(message) }
    }

    @Test
    fun warnFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.warn(format, argument1) }
        verify { imperativeLogger.warn(format, argument1) }
    }

    @Test
    fun warnFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.warn(format, argument1, argument2) }
        verify { imperativeLogger.warn(format, argument1, argument2) }
    }

    @Test
    fun warnFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.warn(format, argument1, argument2, argument3) }
        verify { imperativeLogger.warn(format, argument1, argument2, argument3) }
    }

    @Test
    fun warnMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.warn(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.warn(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun warnMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        step { logger.warn(marker, message) }
        verify { imperativeLogger.warn(marker, message) }
    }

    @Test
    fun warnFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.warn(marker, format, argument1) }
        verify { imperativeLogger.warn(marker, format, argument1) }
    }

    @Test
    fun warnFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.warn(marker, format, argument1, argument2) }
        verify { imperativeLogger.warn(marker, format, argument1, argument2) }
    }

    @Test
    fun warnFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.warn(marker, format, argument1, argument2, argument3) }
        verify { imperativeLogger.warn(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun warnMessageThrowableMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.warn(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Error
    @Test
    fun errorEnabled() {
        every { imperativeLogger.isErrorEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
        assertFalse(logger.isErrorEnabled, "error enabled when it should not be")
        assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
    }

    @Test
    fun errorEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isErrorEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
        assertFalse(logger.isErrorEnabled(marker), "error enabled when it should not be")
        assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
    }

    @Test
    fun errorMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(supplierCaptor)) } returns Unit
        step { logger.error { message } }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.error(exception) { message } }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.error(marker) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.error(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        step { logger.error(marker, exception) { message } }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessage() {
        val message: String = randomText()
        step { logger.error(message) }
        verify { imperativeLogger.error(message) }
    }

    @Test
    fun errorFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.error(format, argument1) }
        verify { imperativeLogger.error(format, argument1) }
    }

    @Test
    fun errorFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.error(format, argument1, argument2) }
        verify { imperativeLogger.error(format, argument1, argument2) }
    }

    @Test
    fun errorFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.error(format, argument1, argument2, argument3) }
        verify { imperativeLogger.error(format, argument1, argument2, argument3) }
    }

    @Test
    fun errorMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.error(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.error(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun errorMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        step { logger.error(marker, message) }
        verify { imperativeLogger.error(marker, message) }
    }

    @Test
    fun errorFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        step { logger.error(marker, format, argument1) }
        verify { imperativeLogger.error(marker, format, argument1) }
    }

    @Test
    fun errorFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        step { logger.error(marker, format, argument1, argument2) }
        verify { imperativeLogger.error(marker, format, argument1, argument2) }
    }

    @Test
    fun errorFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        step { logger.error(marker, format, argument1, argument2, argument3) }
        verify { imperativeLogger.error(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun errorMessageThrowableMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.error(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        step { logger.error(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Other
    @Test
    fun entry() {
        val message1: String = randomText()
        val message2: String = randomText()
        step { logger.entry(message1, message2) }
        verify { imperativeLogger.entry(message1, message2) }
    }

    @Test
    fun exit() {
        step { logger.exit() }
        verify { imperativeLogger.exit() }
    }

    @Test
    fun exitParameterized() {
        val message: String = randomText()
        StepVerifier.create(logger.exit(message))
            .expectNextMatches { it.second == message }
            .verifyComplete()
        verify { imperativeLogger.exit(message) }
    }

    @Test
    fun throwing() {
        val message: String = randomText()
        StepVerifier.create(logger.throwing(SimulatedException(message)))
            .expectNextMatches { it.message == message }
            .verifyComplete()
    }

    @Test
    fun catching() {
        val message: String = randomText()
        StepVerifier.create(logger.catching(SimulatedException(message)))
            .expectNextMatches { it.message == message }
            .verifyComplete()
    }
    //endregion

    class SimulatedException(message: String? = null) : RuntimeException(message)
}