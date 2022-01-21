package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.Consts
import hu.numichi.reactive.logger.exception.ContextNotExistException
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context
import java.util.*
import kotlin.coroutines.coroutineContext

@ExperimentalCoroutinesApi
internal class ReactiveLoggerTest {
    private val imperativeLogger: Logger = mockk(relaxed = true)
    private val logger = ReactiveLogger.builder().withLogger(imperativeLogger).build()
    private val loggerScheduled = ReactiveLogger.builder().withScheduler(Schedulers.parallel()).withLogger(imperativeLogger).build()
    private val loggerWithError = ReactiveLogger.builder().withLogger(imperativeLogger).enableError().build()

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
    fun contextExtractiveOfReactiveLogger() = runTest {
        assertThrows<IllegalArgumentException> {
            ReactiveLogger.builder(ReactorContext).withLogger(imperativeLogger).build().info("")
        }

        assertThrows<IllegalArgumentException> {
            ReactiveLogger.builder(ReactorContext).withContext { null }.withLogger(imperativeLogger).build().info("")
        }
    }

    @Test
    fun builderWithClassLogger() {
        assertNotNull(ReactiveLogger.builder().withLogger(this.javaClass).build())
        assertNotNull(ReactiveLogger.builder().withLogger("any").build())
        assertNotNull(ReactiveLogger.builder().withLogger(LoggerFactory.getLogger(this.javaClass)).build())
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
        val context = Context.of(Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY, mdc)
        assertEquals(Optional.of(mdc), logger.readMDC(context))
        assertEquals(Optional.of(mdc).get(), logger.readMDC(context).get())
    }

    @Test
    fun imperative() {
        assertSame(logger.imperative, imperativeLogger)
        assertSame(loggerWithError.imperative, imperativeLogger)
    }

    @Test
    fun scheduled() {
        assertSame(Schedulers.boundedElastic(), logger.scheduler)
        assertSame(Schedulers.parallel(), loggerScheduled.scheduler)
    }

    @Test
    fun contextKey() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = ReactiveLogger.builder().withMDCContextKey(contextKey).build()

        assertSame(loggerWithCustomScheduler.mdcContextKey, contextKey)

        assertThrows(IllegalArgumentException::class.java) {
            ReactiveLogger.builder().withMDCContextKey("").build()
        }

        assertThrows(IllegalArgumentException::class.java) {
            ReactiveLogger.builder().withMDCContextKey(" ").build()
        }
    }

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

    //region Trace
    @Test
    fun traceMessage() = runTest {
        val message: String = randomText()
        logger.trace(message)
        verify { imperativeLogger.trace(message) }
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
    fun debugMessage() = runTest {
        val message: String = randomText()
        logger.debug(message)
        verify { imperativeLogger.debug(message) }
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
    fun infoMessage() = runTest {
        val message: String = randomText()
        logger.info(message)
        verify { imperativeLogger.info(message) }
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
    fun warnMessage() = runTest {
        val message: String = randomText()
        logger.warn(message)
        verify { imperativeLogger.warn(message) }
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
    fun errorMessage() = runTest {
        val message: String = randomText()
        logger.error(message)
        verify { imperativeLogger.error(message) }
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

    @Test
    fun checkEnableErrorFlagDifferent() = runTest {
        assertThrows<ContextNotExistException> {
            loggerWithError.info(randomText())
        }
    }

    class SimulatedException(message: String?) : RuntimeException(message)
}