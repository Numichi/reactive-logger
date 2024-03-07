package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.randomText
import io.github.numichi.reactive.logger.stepVerifierEmpty
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import org.slf4j.event.Level

class LoggerTest {
    private val imperativeLogger: Logger = mockk(relaxed = true)
    private val logger = ReactiveLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun isEnabledForLevel() {
        every { imperativeLogger.isEnabledForLevel(any()) } returnsMany listOf(true, false, true)
        assertTrue(logger.isEnabledForLevel(Level.TRACE))
        assertFalse(logger.isEnabledForLevel(Level.TRACE))
        assertTrue(logger.isEnabledForLevel(Level.TRACE))
    }

    @Test
    fun traceEnabled() {
        every { imperativeLogger.isTraceEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isTraceEnabled())
        assertFalse(logger.isTraceEnabled())
        assertTrue(logger.isTraceEnabled())
    }

    @Test
    fun traceEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isTraceEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isTraceEnabled(marker))
        assertFalse(logger.isTraceEnabled(marker))
        assertTrue(logger.isTraceEnabled(marker))
    }

    @Test
    fun debugEnabled() {
        every { imperativeLogger.isDebugEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isDebugEnabled())
        assertFalse(logger.isDebugEnabled())
        assertTrue(logger.isDebugEnabled())
    }

    @Test
    fun debugEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isDebugEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isDebugEnabled(marker))
        assertFalse(logger.isDebugEnabled(marker))
        assertTrue(logger.isDebugEnabled(marker))
    }

    @Test
    fun infoEnabled() {
        every { imperativeLogger.isInfoEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isInfoEnabled())
        assertFalse(logger.isInfoEnabled())
        assertTrue(logger.isInfoEnabled())
    }

    @Test
    fun infoEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isInfoEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isInfoEnabled(marker))
        assertFalse(logger.isInfoEnabled(marker))
        assertTrue(logger.isInfoEnabled(marker))
    }

    @Test
    fun warnEnabled() {
        every { imperativeLogger.isWarnEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isWarnEnabled())
        assertFalse(logger.isWarnEnabled())
        assertTrue(logger.isWarnEnabled())
    }

    @Test
    fun warnEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isWarnEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isWarnEnabled(marker))
        assertFalse(logger.isWarnEnabled(marker))
        assertTrue(logger.isWarnEnabled(marker))
    }

    @Test
    fun errorEnabled() {
        every { imperativeLogger.isErrorEnabled } returnsMany listOf(true, false, true)
        assertTrue(logger.isErrorEnabled())
        assertFalse(logger.isErrorEnabled())
        assertTrue(logger.isErrorEnabled())
    }

    @Test
    fun errorEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isErrorEnabled(marker) } returnsMany listOf(true, false, true)
        assertTrue(logger.isErrorEnabled(marker))
        assertFalse(logger.isErrorEnabled(marker))
        assertTrue(logger.isErrorEnabled(marker))
    }

    @Test
    fun traceMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.trace(message) }
        verify { imperativeLogger.trace(message) }
    }

    @Test
    fun traceFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.trace(format, argument1) }
        verify { imperativeLogger.trace(format, argument1) }
    }

    @Test
    fun traceFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.trace(format, argument1, argument2) }
        verify { imperativeLogger.trace(format, argument1, argument2) }
    }

    @Test
    fun traceFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.trace(format, argument1, argument2, argument3) }
        verify { imperativeLogger.trace(format, argument1, argument2, argument3) }
    }

    @Test
    fun traceMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.trace(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        stepVerifierEmpty { logger.trace(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun traceMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.trace(marker, message) }
        verify { imperativeLogger.trace(marker, message) }
    }

    @Test
    fun traceFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.trace(marker, format, argument1) }
        verify { imperativeLogger.trace(marker, format, argument1) }
    }

    @Test
    fun traceFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.trace(marker, format, argument1, argument2) }
        verify { imperativeLogger.trace(marker, format, argument1, argument2) }
    }

    @Test
    fun traceFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.trace(marker, format, argument1, argument2, argument3) }
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
        stepVerifierEmpty { logger.trace(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Debug
    @Test
    fun debugMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.debug(message) }
        verify { imperativeLogger.debug(message) }
    }

    @Test
    fun debugFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.debug(format, argument1) }
        verify { imperativeLogger.debug(format, argument1) }
    }

    @Test
    fun debugFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.debug(format, argument1, argument2) }
        verify { imperativeLogger.debug(format, argument1, argument2) }
    }

    @Test
    fun debugFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.debug(format, argument1, argument2, argument3) }
        verify { imperativeLogger.debug(format, argument1, argument2, argument3) }
    }

    @Test
    fun debugMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.debug(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        stepVerifierEmpty { logger.debug(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun debugMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.debug(marker, message) }
        verify { imperativeLogger.debug(marker, message) }
    }

    @Test
    fun debugFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.debug(marker, format, argument1) }
        verify { imperativeLogger.debug(marker, format, argument1) }
    }

    @Test
    fun debugFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.debug(marker, format, argument1, argument2) }
        verify { imperativeLogger.debug(marker, format, argument1, argument2) }
    }

    @Test
    fun debugFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.debug(marker, format, argument1, argument2, argument3) }
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
        stepVerifierEmpty { logger.debug(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Info
    @Test
    fun infoMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.info(message) }
        verify { imperativeLogger.info(message) }
    }

    @Test
    fun infoFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.info(format, argument1) }
        verify { imperativeLogger.info(format, argument1) }
    }

    @Test
    fun infoFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.info(format, argument1, argument2) }
        verify { imperativeLogger.info(format, argument1, argument2) }
    }

    @Test
    fun infoFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.info(format, argument1, argument2, argument3) }
        verify { imperativeLogger.info(format, argument1, argument2, argument3) }
    }

    @Test
    fun infoMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.info(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        stepVerifierEmpty { logger.info(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun infoMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.info(marker, message) }
        verify { imperativeLogger.info(marker, message) }
    }

    @Test
    fun infoFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.info(marker, format, argument1, argument2) }
        verify { imperativeLogger.info(marker, format, argument1, argument2) }
    }

    @Test
    fun infoFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.info(marker, format, argument1) }
        verify { imperativeLogger.info(marker, format, argument1) }
    }

    @Test
    fun infoFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.info(marker, format, argument1, argument2, argument3) }
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
        stepVerifierEmpty { logger.info(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Warn
    @Test
    fun warnMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.warn(message) }
        verify { imperativeLogger.warn(message) }
    }

    @Test
    fun warnFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.warn(format, argument1) }
        verify { imperativeLogger.warn(format, argument1) }
    }

    @Test
    fun warnFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.warn(format, argument1, argument2) }
        verify { imperativeLogger.warn(format, argument1, argument2) }
    }

    @Test
    fun warnFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.warn(format, argument1, argument2, argument3) }
        verify { imperativeLogger.warn(format, argument1, argument2, argument3) }
    }

    @Test
    fun warnMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.warn(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        stepVerifierEmpty { logger.warn(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun warnMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.warn(marker, message) }
        verify { imperativeLogger.warn(marker, message) }
    }

    @Test
    fun warnFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.warn(marker, format, argument1) }
        verify { imperativeLogger.warn(marker, format, argument1) }
    }

    @Test
    fun warnFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.warn(marker, format, argument1, argument2) }
        verify { imperativeLogger.warn(marker, format, argument1, argument2) }
    }

    @Test
    fun warnFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.warn(marker, format, argument1, argument2, argument3) }
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
        stepVerifierEmpty { logger.warn(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Error
    @Test
    fun errorMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.error(message) }
        verify { imperativeLogger.error(message) }
    }

    @Test
    fun errorFormatArgument1Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.error(format, argument1) }
        verify { imperativeLogger.error(format, argument1) }
    }

    @Test
    fun errorFormatArgument2Array() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.error(format, argument1, argument2) }
        verify { imperativeLogger.error(format, argument1, argument2) }
    }

    @Test
    fun errorFormatArgumentArray() {
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.error(format, argument1, argument2, argument3) }
        verify { imperativeLogger.error(format, argument1, argument2, argument3) }
    }

    @Test
    fun errorMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val stringCaptor = slot<String>()
        every { imperativeLogger.error(capture(stringCaptor), capture(exceptionCaptor)) } returns Unit
        stepVerifierEmpty { logger.error(message, exception) }
        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(stringCaptor.captured, message)
    }

    @Test
    fun errorMessageMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.error(marker, message) }
        verify { imperativeLogger.error(marker, message) }
    }

    @Test
    fun errorFormatArgument1ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        stepVerifierEmpty { logger.error(marker, format, argument1) }
        verify { imperativeLogger.error(marker, format, argument1) }
    }

    @Test
    fun errorFormatArgument2ArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        stepVerifierEmpty { logger.error(marker, format, argument1, argument2) }
        verify { imperativeLogger.error(marker, format, argument1, argument2) }
    }

    @Test
    fun errorFormatArgumentArrayMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        val format: String = randomText()
        val argument1: String = randomText()
        val argument2: String = randomText()
        val argument3: String = randomText()
        stepVerifierEmpty { logger.error(marker, format, argument1, argument2, argument3) }
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
        stepVerifierEmpty { logger.error(marker, message, exception) }
        assertEquals(markerCaptor.captured, marker)
        assertEquals(messageCaptor.captured, message)
        assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    class SimulatedException(message: String?) : RuntimeException(message)
}