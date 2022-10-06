package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.randomText
import io.github.numichi.reactive.logger.stepVerifier
import io.github.numichi.reactive.logger.stepVerifierEmpty
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import mu.KLogger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Marker
import org.slf4j.MarkerFactory

class KLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = ReactiveKLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    //region Trace
    @Test
    fun traceEnabled() {
        every { imperativeLogger.isTraceEnabled } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
        Assertions.assertFalse(logger.isTraceEnabled, "trace enabled when it should not be")
        Assertions.assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
    }

    @Test
    fun traceEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isTraceEnabled(marker) } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
        Assertions.assertFalse(logger.isTraceEnabled(marker), "trace enabled when it should not be")
        Assertions.assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
    }

    @Test
    fun traceMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.trace { message } }
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.trace(exception) { message } }
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.trace(marker) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
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
        stepVerifierEmpty { logger.trace(marker, exception) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(exception, exceptionCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
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
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(stringCaptor.captured, message)
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
        Assertions.assertEquals(markerCaptor.captured, marker)
        Assertions.assertEquals(messageCaptor.captured, message)
        Assertions.assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Debug
    @Test
    fun debugEnabled() {
        every { imperativeLogger.isDebugEnabled } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
        Assertions.assertFalse(logger.isDebugEnabled, "debug enabled when it should not be")
        Assertions.assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
    }

    @Test
    fun debugEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isDebugEnabled(marker) } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
        Assertions.assertFalse(logger.isDebugEnabled(marker), "debug enabled when it should not be")
        Assertions.assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
    }

    @Test
    fun debugMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.debug { message } }
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.debug(exception) { message } }
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.debug(marker) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
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
        stepVerifierEmpty { logger.debug(marker, exception) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(exception, exceptionCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

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
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(stringCaptor.captured, message)
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
        Assertions.assertEquals(markerCaptor.captured, marker)
        Assertions.assertEquals(messageCaptor.captured, message)
        Assertions.assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Info
    @Test
    fun infoEnabled() {
        every { imperativeLogger.isInfoEnabled } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
        Assertions.assertFalse(logger.isInfoEnabled, "info enabled when it should not be")
        Assertions.assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
    }

    @Test
    fun infoEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isInfoEnabled(marker) } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
        Assertions.assertFalse(logger.isInfoEnabled(marker), "info enabled when it should not be")
        Assertions.assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
    }

    @Test
    fun infoMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.info { message } }
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.info(exception) { message } }
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.info(marker) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
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
        stepVerifierEmpty { logger.info(marker, exception) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(exception, exceptionCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

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
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(stringCaptor.captured, message)
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
        Assertions.assertEquals(markerCaptor.captured, marker)
        Assertions.assertEquals(messageCaptor.captured, message)
        Assertions.assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Warn
    @Test
    fun warnEnabled() {
        every { imperativeLogger.isWarnEnabled } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
        Assertions.assertFalse(logger.isWarnEnabled, "warn enabled when it should not be")
        Assertions.assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
    }

    @Test
    fun warnEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isWarnEnabled(marker) } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
        Assertions.assertFalse(logger.isWarnEnabled(marker), "warn enabled when it should not be")
        Assertions.assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
    }

    @Test
    fun warnMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.warn { message } }
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.warn(exception) { message } }
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.warn(marker) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
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
        stepVerifierEmpty { logger.warn(marker, exception) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(exception, exceptionCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

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
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(stringCaptor.captured, message)
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
        Assertions.assertEquals(markerCaptor.captured, marker)
        Assertions.assertEquals(messageCaptor.captured, message)
        Assertions.assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Error
    @Test
    fun errorEnabled() {
        every { imperativeLogger.isErrorEnabled } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
        Assertions.assertFalse(logger.isErrorEnabled, "error enabled when it should not be")
        Assertions.assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
    }

    @Test
    fun errorEnabledMarker() {
        val marker = MarkerFactory.getMarker(randomText())
        every { imperativeLogger.isErrorEnabled(marker) } returnsMany listOf(true, false, true)
        Assertions.assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
        Assertions.assertFalse(logger.isErrorEnabled(marker), "error enabled when it should not be")
        Assertions.assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
    }

    @Test
    fun errorMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.error { message } }
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.error(exception) { message } }
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageMarkerSupplier() {
        val marker = MarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(markerCaptor), capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.error(marker) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
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
        stepVerifierEmpty { logger.error(marker, exception) { message } }
        Assertions.assertEquals(marker, markerCaptor.captured)
        Assertions.assertEquals(exception, exceptionCaptor.captured)
        Assertions.assertEquals(message, supplierCaptor.captured())
    }

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
        Assertions.assertEquals(exceptionCaptor.captured.message, exception.message)
        Assertions.assertEquals(stringCaptor.captured, message)
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
        Assertions.assertEquals(markerCaptor.captured, marker)
        Assertions.assertEquals(messageCaptor.captured, message)
        Assertions.assertEquals(exceptionCaptor.captured, exception)
    }
    //endregion

    //region Other
    @Test
    fun entry() {
        val message1: String = randomText()
        val message2: String = randomText()
        stepVerifierEmpty { logger.entry(message1, message2) }
        verify { imperativeLogger.entry(message1, message2) }
    }

    @Test
    fun exit() {
        stepVerifierEmpty { logger.exit() }
        verify { imperativeLogger.exit() }
    }

    @Test
    fun exitParameterized() {
        val message: String = randomText()
        stepVerifier(message) { logger.exit(message) }
        verify { imperativeLogger.exit(message) }
    }

    @Test
    fun throwing() {
        val message: String = randomText()
        val throwable = SimulatedException(message)
        stepVerifier(throwable) { logger.throwing(throwable) }
        verify { imperativeLogger.throwing(throwable) }
    }

    @Test
    fun catching() {
        val message: String = randomText()
        val throwable = SimulatedException(message)
        stepVerifierEmpty { logger.catching(throwable) }
        verify { imperativeLogger.catching(throwable) }
    }
    //endregion

    class SimulatedException(message: String? = null) : RuntimeException(message)
}