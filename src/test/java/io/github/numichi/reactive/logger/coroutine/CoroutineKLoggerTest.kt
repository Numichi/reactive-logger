package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.MDCContextTest.Companion.ANOTHER_CONTEXT_KEY
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.test.runTest
import mu.KLogger
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context
import java.util.*

@ExperimentalCoroutinesApi
internal class CoroutineKLoggerTest {
    private val imperativeLogger: KLogger =
        mockk(relaxed = true)
    private val logger: ICoroutineKLogger =
        CoroutineKLogger.reactorBuilder().withLogger(imperativeLogger).build()
    private val loggerScheduled: ICoroutineKLogger =
        CoroutineKLogger.reactorBuilder().withLogger(imperativeLogger).withScheduler(Schedulers.parallel()).build()
    private val loggerWithError: ICoroutineKLogger =
        CoroutineKLogger.reactorBuilder().withLogger(imperativeLogger).withEnableError(true).build()

    companion object {
        @JvmStatic
        fun randomText(): String {
            return UUID.randomUUID().toString()
        }

        @JvmStatic
        fun randomMap(i: Int): MutableMap<String, String> {
            var index = i
            val expected: MutableMap<String, String> = HashMap()
            while (0 < index) {
                expected[randomText()] = randomText()
                index--
            }
            return expected
        }
    }

    @BeforeEach
    fun beforeEach() {
        clearMocks(imperativeLogger)
    }

    @Test
    fun snapshot() = runTest {
        val mdc = MDC()
        mdc[randomText()] = randomText()
        var context1 = Context.empty()
        context1 = context1.put(DefaultValues.getInstance().defaultReactorContextMdcKey, mdc)
        var context2 = Context.empty()
        context2 = context2.put(ANOTHER_CONTEXT_KEY, mdc)

        var mdcResult = logger.snapshot(context1)
        assertEquals(mdc, mdcResult)

        mdcResult = logger.snapshot(context2)
        assertEquals(mapOf<String, String>(), mdcResult)

        assertEquals(null, loggerWithError.snapshot(null))

        withMDCContext(context1) {
            mdcResult = logger.snapshot(null)
            assertEquals(mdc, mdcResult)
        }

        withMDCContext(context2) {
            mdcResult = logger.snapshot(null)
            assertEquals(mapOf<String, String>(), logger.snapshot())
        }

        withMDCContext(context1) {
            mdcResult = logger.snapshot()
            assertEquals(mdc, mdcResult)
        }

        withMDCContext(context2) {
            mdcResult = logger.snapshot()
            assertEquals(mapOf<String, String>(), logger.snapshot())
        }
    }

    @Test
    fun `should configure empty context if not exist context or null`() = runTest {
        val instance1 =
            CoroutineLogger.builder(ReactorContext) { coroutineContext[it]?.context }.withLogger(imperativeLogger).build()

        val instance2 =
            CoroutineLogger.builder(ReactorContext) { null }.withLogger(imperativeLogger).build()

        val instance3 =
            CoroutineLogger.builder(ReactorContext) { Context.empty() }.withLogger(imperativeLogger).build()

        instance1.info("")
        instance2.info("")
        instance3.info("")
    }

    @Test
    fun createReactiveLogger() = runTest {
        val instance1 = CoroutineLogger.reactorBuilder().build()
        assertNotNull(instance1)
        assertEquals(DefaultValues.getInstance().defaultReactorContextMdcKey, instance1.mdcContextKey)
    }

    @Test
    fun getName() {
        val name: String = randomText()
        every { imperativeLogger.name } returns name
        assertEquals(name, logger.name)
    }

    @Test
    fun readMDC() {
        val mdc: Map<String, String> = randomMap(1)
        val context = Context.of(DefaultValues.getInstance().defaultReactorContextMdcKey, mdc)
        assertEquals(Optional.of(mdc), logger.readMDC(context))
        assertEquals(Optional.of(mdc).get(), logger.readMDC(context).get())
    }

    @Test
    fun imperative() {
        assertSame(logger.reactorLogger.logger, imperativeLogger)
        assertSame(loggerWithError.reactorLogger.logger, imperativeLogger)
    }

    @Test
    fun scheduled() {
        assertSame(Schedulers.boundedElastic(), logger.scheduler)
        assertSame(Schedulers.parallel(), loggerScheduled.scheduler)
    }

    @Test
    fun contextKey() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = CoroutineLogger.reactorBuilder().withMDCContextKey(contextKey).build()
        assertSame(loggerWithCustomScheduler.mdcContextKey, contextKey)
    }

    @Test
    fun traceEnabled() {
        every { imperativeLogger.isTraceEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled, "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
    }

    @Test
    suspend fun traceEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isTraceEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled(marker), "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
    }

    @Test
    fun debugEnabled() {
        every { imperativeLogger.isDebugEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
        assertFalse(logger.isDebugEnabled, "debug enabled when it should not be")
        assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
    }

    @Test
    suspend fun debugEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isDebugEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
        assertFalse(logger.isDebugEnabled(marker), "debug enabled when it should not be")
        assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
    }

    @Test
    fun infoEnabled() {
        every { imperativeLogger.isInfoEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
        assertFalse(logger.isInfoEnabled, "info enabled when it should not be")
        assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
    }

    @Test
    suspend fun infoEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isInfoEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
        assertFalse(logger.isInfoEnabled(marker), "info enabled when it should not be")
        assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
    }

    @Test
    fun warnEnabled() {
        every { imperativeLogger.isWarnEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
        assertFalse(logger.isWarnEnabled, "warn enabled when it should not be")
        assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
    }

    @Test
    suspend fun warnEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isWarnEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
        assertFalse(logger.isWarnEnabled(marker), "warn enabled when it should not be")
        assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
    }

    @Test
    fun errorEnabled() {
        every { imperativeLogger.isErrorEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
        assertFalse(logger.isErrorEnabled, "error enabled when it should not be")
        assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
    }

    @Test
    suspend fun errorEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isErrorEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
        assertFalse(logger.isErrorEnabled(marker), "error enabled when it should not be")
        assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
    }

    //region Trace
    @Test
    fun traceMessageSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(supplierCaptor)) } returns Unit
        logger.trace { message }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.trace(exception) { message }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        logger.trace(marker) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.trace(marker, exception) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessage() = runTest {
        val message: String = randomText()
        logger.trace(message)
        verify { imperativeLogger.trace(message) }
    }

    @Test
    fun traceFormatArgument1Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        logger.trace(format, argument1)
        verify { imperativeLogger.trace(format, argument1) }
    }

    @Test
    fun traceFormatArgument2Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.trace(format, argument1, argument2)
        verify { imperativeLogger.trace(format, argument1, argument2) }
    }

    @Test
    fun traceFormatArgumentArray() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.trace(format, argument1, argument2, argument3)
        verify { imperativeLogger.trace(format, argument1, argument2, argument3) }
    }

    @Test
    fun traceMessageThrowable() = runTest {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.trace(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        logger.trace(message, exception)
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun traceMessageMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        logger.trace(marker, message)
        verify { imperativeLogger.trace(marker, message) }
    }

    @Test
    fun traceFormatArgument1ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        logger.trace(marker, format, argument1)
        verify { imperativeLogger.trace(marker, format, argument1) }
    }

    @Test
    fun traceFormatArgument2ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.trace(marker, format, argument1, argument2)
        verify { imperativeLogger.trace(marker, format, argument1, argument2) }
    }

    @Test
    fun traceFormatArgumentArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.trace(marker, format, argument1, argument2, argument3)
        verify { imperativeLogger.trace(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun traceMessageThrowableMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        logger.trace(marker, message, exception)
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Debug
    @Test
    fun debugMessageSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(supplierCaptor)) } returns Unit
        logger.debug { message }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.debug(exception) { message }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        logger.debug(marker) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.debug(marker, exception) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessage() = runTest {
        val message: String = randomText()
        logger.debug(message)
        verify { imperativeLogger.debug(message) }
    }

    @Test
    fun debugFormatArgument1Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        logger.debug(format, argument1)
        verify { imperativeLogger.debug(format, argument1) }
    }

    @Test
    fun debugFormatArgument2Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.debug(format, argument1, argument2)
        verify { imperativeLogger.debug(format, argument1, argument2) }
    }

    @Test
    fun debugFormatArgumentArray() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.debug(format, argument1, argument2, argument3)
        verify { imperativeLogger.debug(format, argument1, argument2, argument3) }
    }

    @Test
    fun debugMessageThrowable() = runTest {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.debug(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        logger.debug(message, exception)
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun debugMessageMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        logger.debug(marker, message)
        verify { imperativeLogger.debug(marker, message) }
    }

    @Test
    fun debugFormatArgument1ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        logger.debug(marker, format, argument1)
        verify { imperativeLogger.debug(marker, format, argument1) }
    }

    @Test
    fun debugFormatArgument2ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.debug(marker, format, argument1, argument2)
        verify { imperativeLogger.debug(marker, format, argument1, argument2) }
    }

    @Test
    fun debugFormatArgumentArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.debug(marker, format, argument1, argument2, argument3)
        verify { imperativeLogger.debug(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun debugMessageThrowableMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        logger.debug(marker, message, exception)
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Info
    @Test
    fun infoMessageSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(supplierCaptor)) } returns Unit
        logger.info { message }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.info(exception) { message }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        logger.info(marker) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        every { imperativeLogger.info(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.info(marker, exception) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessage() = runTest {
        val message: String = randomText()
        logger.info(message)
        verify { imperativeLogger.info(message) }
    }

    @Test
    fun infoFormatArgument1Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        logger.info(format, argument1)
        verify { imperativeLogger.info(format, argument1) }
    }

    @Test
    fun infoFormatArgument2Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.info(format, argument1, argument2)
        verify { imperativeLogger.info(format, argument1, argument2) }
    }

    @Test
    fun infoFormatArgumentArray() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.info(format, argument1, argument2, argument3)
        verify { imperativeLogger.info(format, argument1, argument2, argument3) }
    }

    @Test
    fun infoMessageThrowable() = runTest {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.info(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        logger.info(message, exception)
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun infoMessageMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        logger.info(marker, message)
        verify { imperativeLogger.info(marker, message) }
    }

    @Test
    fun infoFormatArgument1ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        logger.info(marker, format, argument1)
        verify { imperativeLogger.info(marker, format, argument1) }
    }

    @Test
    fun infoFormatArgument2ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.info(marker, format, argument1, argument2)
        verify { imperativeLogger.info(marker, format, argument1, argument2) }
    }

    @Test
    fun infoFormatArgumentArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.info(marker, format, argument1, argument2, argument3)
        verify { imperativeLogger.info(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun infoMessageThrowableMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.info(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        logger.info(marker, message, exception)
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Warn
    @Test
    fun warnMessageSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(supplierCaptor)) } returns Unit
        logger.warn { message }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.warn(exception) { message }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        logger.warn(marker) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.warn(marker, exception) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessage() = runTest {
        val message: String = randomText()
        logger.warn(message)
        verify { imperativeLogger.warn(message) }
    }

    @Test
    fun warnFormatArgument1Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        logger.warn(format, argument1)
        verify { imperativeLogger.warn(format, argument1) }
    }

    @Test
    fun warnFormatArgument2Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.warn(format, argument1, argument2)
        verify { imperativeLogger.warn(format, argument1, argument2) }
    }

    @Test
    fun warnFormatArgumentArray() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.warn(format, argument1, argument2, argument3)
        verify { imperativeLogger.warn(format, argument1, argument2, argument3) }
    }

    @Test
    fun warnMessageThrowable() = runTest {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.warn(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        logger.warn(message, exception)
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun warnMessageMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        logger.warn(marker, message)
        verify { imperativeLogger.warn(marker, message) }
    }

    @Test
    fun warnFormatArgument1ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        logger.warn(marker, format, argument1)
        verify { imperativeLogger.warn(marker, format, argument1) }
    }

    @Test
    fun warnFormatArgument2ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.warn(marker, format, argument1, argument2)
        verify { imperativeLogger.warn(marker, format, argument1, argument2) }
    }

    @Test
    fun warnFormatArgumentArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.warn(marker, format, argument1, argument2, argument3)
        verify { imperativeLogger.warn(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun warnMessageThrowableMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        logger.warn(marker, message, exception)
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Error
    @Test
    fun errorMessageSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(supplierCaptor)) } returns Unit
        logger.error { message }
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableSupplier() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.error(exception) { message }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        logger.error(marker) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableMarkerSupplier() = runTest {
        val marker = MarkerFactory.getMarker(ReactiveKLoggerTest.randomText())
        val message: String = ReactiveKLoggerTest.randomText()
        val exception = ReactiveKLoggerTest.SimulatedException(ReactiveKLoggerTest.randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<ReactiveKLoggerTest.SimulatedException>()
        every { imperativeLogger.error(capture(markerCaptor), capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        logger.error(marker, exception) { message }
        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessage() = runTest {
        val message: String = randomText()
        logger.error(message)
        verify { imperativeLogger.error(message) }
    }

    @Test
    fun errorFormatArgument1Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        logger.error(format, argument1)
        verify { imperativeLogger.error(format, argument1) }
    }

    @Test
    fun errorFormatArgument2Array() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.error(format, argument1, argument2)
        verify { imperativeLogger.error(format, argument1, argument2) }
    }

    @Test
    fun errorFormatArgumentArray() = runTest {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.error(format, argument1, argument2, argument3)
        verify { imperativeLogger.error(format, argument1, argument2, argument3) }
    }

    @Test
    fun errorMessageThrowable() = runTest {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.error(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        logger.error(message, exception)
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun errorMessageMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        logger.error(marker, message)
        verify { imperativeLogger.error(marker, message) }
    }

    @Test
    fun errorFormatArgument1ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        logger.error(marker, format, argument1)
        verify { imperativeLogger.error(marker, format, argument1) }
    }

    @Test
    fun errorFormatArgument2ArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        logger.error(marker, format, argument1, argument2)
        verify { imperativeLogger.error(marker, format, argument1, argument2) }
    }

    @Test
    fun errorFormatArgumentArrayMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        logger.error(marker, format, argument1, argument2, argument3)
        verify { imperativeLogger.error(marker, format, argument1, argument2, argument3) }
    }

    @Test
    fun errorMessageThrowableMarker() = runTest {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val messageCaptor = slot<String>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.error(capture(markerCaptor), capture(messageCaptor), capture(exceptionCaptor)) } returns Unit
        logger.error(marker, message, exception)
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Other
    @Test
    fun entry() = runTest {
        val message1: String = ReactiveKLoggerTest.randomText()
        val message2: String = ReactiveKLoggerTest.randomText()
        logger.entry(message1, message2)
        verify { imperativeLogger.entry(message1, message2) }
    }

    @Test
    fun exit() = runTest {
        logger.exit()
        verify { imperativeLogger.exit() }
    }

    @Test
    fun exitParameterized() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val result = logger.exit(message)
        verify { imperativeLogger.exit(message) }
        assertEquals(result, message)
    }

    @Test
    fun throwing() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val result = logger.throwing(ReactiveKLoggerTest.SimulatedException(message))
        assertEquals(message, result.message)
    }

    @Test
    fun catching() = runTest {
        val message: String = ReactiveKLoggerTest.randomText()
        val result = logger.catching(ReactiveKLoggerTest.SimulatedException(message))
        assertEquals(message, result.message)
    }
    //endregion

    @Test
    fun checkEnableErrorFlagDifferent() = runTest {
        assertThrows<ContextNotExistException> {
            loggerWithError.info(randomText())
        }
    }

    class SimulatedException(message: String?) : RuntimeException(message)
}
