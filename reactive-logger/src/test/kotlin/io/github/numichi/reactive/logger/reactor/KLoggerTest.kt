package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.coroutine.KLoggerTest
import io.github.numichi.reactive.logger.randomText
import io.github.numichi.reactive.logger.stepVerifier
import io.github.numichi.reactive.logger.stepVerifierEmpty
import io.github.oshai.kotlinlogging.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = ReactiveKLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun afterEach() {
        Configuration.reset()
        clearMocks(imperativeLogger)
    }

    @Test
    fun isLoggingEnabledFor() {
        val level = Level.TRACE
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isLoggingEnabledFor(level, marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isLoggingEnabledFor(level, marker))
        assertFalse(logger.isLoggingEnabledFor(level, marker))
        assertTrue(logger.isLoggingEnabledFor(level, marker))
    }

    @Test
    fun isLoggingOff() {
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isLoggingOff(marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isLoggingOff(marker))
        assertFalse(logger.isLoggingOff(marker))
        assertTrue(logger.isLoggingOff(marker))
    }

    //region Trace
    @Test
    fun traceKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = KLoggerTest.SimulatedException(randomText())
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atTrace(capture(supplierCaptor)) } returns Unit

            stepVerifierEmpty {
                logger.atTrace {
                    this.message = message
                    this.cause = exception
                    this.payload = mapOf("A" to "B")
                }
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
        }
    }

    @Test
    fun traceMarkerKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val marker = KMarkerFactory.getMarker(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atTrace(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atTrace(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
        assertEquals(marker, markerCaptor.captured)
    }

    @Test
    fun traceEnabled() {
        every { imperativeLogger.isTraceEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled, "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
    }

    @Test
    fun traceEnabledMarker() {
        val kMarker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isTraceEnabled(kMarker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isTraceEnabled(kMarker), "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled(kMarker), "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled(kMarker), "trace not enabled when it should be")
    }

    @Test
    fun traceMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.trace { message } }

        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.trace(exception) { message } }

        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.trace(null as Throwable?, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun traceMessageThrowableMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.trace(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun traceMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.trace(message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.trace(capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun traceMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.trace(exception) { message } }

        assertEquals(exception.message, exceptionCaptor.captured.message)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun traceMessageMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.trace(marker, message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.trace(null as Throwable?, marker, capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun traceMessageThrowableMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.trace(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.trace(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }
    //endregion

    //region Debug
    @Test
    fun debugKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atDebug(capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atDebug {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
    }

    @Test
    fun debugMarkerKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val marker = KMarkerFactory.getMarker(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atDebug(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atDebug(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
        assertEquals(marker, markerCaptor.captured)
    }

    @Test
    fun debugEnabled() {
        every { imperativeLogger.isDebugEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
        assertFalse(logger.isDebugEnabled, "debug enabled when it should not be")
        assertTrue(logger.isDebugEnabled, "debug not enabled when it should be")
    }

    @Test
    fun debugEnabledMarker() {
        val kMarker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isDebugEnabled(kMarker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isDebugEnabled(kMarker), "debug not enabled when it should be")
        assertFalse(logger.isDebugEnabled(kMarker), "debug enabled when it should not be")
        assertTrue(logger.isDebugEnabled(kMarker), "debug not enabled when it should be")
    }

    @Test
    fun debugMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.debug { message } }

        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.debug(exception) { message } }

        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.debug(null as Throwable?, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun debugMessageThrowableMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.debug(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun debugMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.debug(message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.debug(capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun debugMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.debug(exception) { message } }

        assertEquals(exception.message, exceptionCaptor.captured.message)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun debugMessageMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.debug(marker, message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.debug(null as Throwable?, marker, capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun debugMessageThrowableMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.debug(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.debug(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }
    //endregion

    //region Info
    @Test
    fun infoKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atInfo(capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atInfo {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
    }

    @Test
    fun infoMarkerKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val marker = KMarkerFactory.getMarker(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atInfo(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atInfo(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
        assertEquals(marker, markerCaptor.captured)
    }

    @Test
    fun infoEnabled() {
        every { imperativeLogger.isInfoEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
        assertFalse(logger.isInfoEnabled, "info enabled when it should not be")
        assertTrue(logger.isInfoEnabled, "info not enabled when it should be")
    }

    @Test
    fun infoEnabledMarker() {
        val kMarker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isInfoEnabled(kMarker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isInfoEnabled(kMarker), "info not enabled when it should be")
        assertFalse(logger.isInfoEnabled(kMarker), "info enabled when it should not be")
        assertTrue(logger.isInfoEnabled(kMarker), "info not enabled when it should be")
    }

    @Test
    fun infoMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.info { message } }

        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.info(exception) { message } }

        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.info(null as Throwable?, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun infoMessageThrowableMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.info(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun infoMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.info(message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.info(capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun infoMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.info(exception) { message } }

        assertEquals(exception.message, exceptionCaptor.captured.message)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun infoMessageMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.info(marker, message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.info(null as Throwable?, marker, capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun infoMessageThrowableMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.info(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.info(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }
    //endregion

    //region Warn
    @Test
    fun warnKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atWarn(capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atWarn {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
    }

    @Test
    fun warnMarkerKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val marker = KMarkerFactory.getMarker(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atWarn(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atWarn(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
        assertEquals(marker, markerCaptor.captured)
    }

    @Test
    fun warnEnabled() {
        every { imperativeLogger.isWarnEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
        assertFalse(logger.isWarnEnabled, "warn enabled when it should not be")
        assertTrue(logger.isWarnEnabled, "warn not enabled when it should be")
    }

    @Test
    fun warnEnabledMarker() {
        val kMarker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isWarnEnabled(kMarker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isWarnEnabled(kMarker), "warn not enabled when it should be")
        assertFalse(logger.isWarnEnabled(kMarker), "warn enabled when it should not be")
        assertTrue(logger.isWarnEnabled(kMarker), "warn not enabled when it should be")
    }

    @Test
    fun warnMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.warn { message } }

        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.warn(exception) { message } }

        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.warn(null as Throwable?, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun warnMessageThrowableMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.warn(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun warnMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.warn(message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.warn(capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun warnMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.warn(exception) { message } }

        assertEquals(exception.message, exceptionCaptor.captured.message)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun warnMessageMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.warn(marker, message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.warn(null as Throwable?, marker, capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun warnMessageThrowableMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.warn(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.warn(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }
    //endregion

    //region Error
    @Test
    fun errorKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atError(capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atError {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
    }

    @Test
    fun errorMarkerKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val marker = KMarkerFactory.getMarker(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.atError(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.atError(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
        assertEquals(marker, markerCaptor.captured)
    }

    @Test
    fun errorEnabled() {
        every { imperativeLogger.isErrorEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
        assertFalse(logger.isErrorEnabled, "error enabled when it should not be")
        assertTrue(logger.isErrorEnabled, "error not enabled when it should be")
    }

    @Test
    fun errorEnabledMarker() {
        val kMarker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isErrorEnabled(kMarker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isErrorEnabled(kMarker), "error not enabled when it should be")
        assertFalse(logger.isErrorEnabled(kMarker), "error enabled when it should not be")
        assertTrue(logger.isErrorEnabled(kMarker), "error not enabled when it should be")
    }

    @Test
    fun errorMessageSupplier() {
        val message: String = randomText()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(supplierCaptor)) } returns Unit
        stepVerifierEmpty { logger.error { message } }

        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableSupplier() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.error(exception) { message } }

        assertEquals(exceptionCaptor.captured.message, exception.message)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.error(null as Throwable?, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(message, supplierCaptor.captured())
    }

    @Test
    fun errorMessageThrowableMarkerSupplier() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.error(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun errorMessage() {
        val message: String = randomText()
        stepVerifierEmpty { logger.error(message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.error(capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun errorMessageThrowable() {
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val exceptionCaptor = slot<SimulatedException>()
        val supplierCaptor = slot<() -> Any?>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.error(exception) { message } }

        assertEquals(exception.message, exceptionCaptor.captured.message)
        assertEquals(message, (supplierCaptor.captured)())
    }

    @Test
    fun errorMessageMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        stepVerifierEmpty { logger.error(marker, message) }

        val stringCaptor = slot<() -> Any?>()
        verify { imperativeLogger.error(null as Throwable?, marker, capture(stringCaptor)) }

        assertEquals(message, stringCaptor.captured())
    }

    @Test
    fun errorMessageThrowableMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        val message: String = randomText()
        val exception = SimulatedException(randomText())
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<() -> Any?>()
        val exceptionCaptor = slot<SimulatedException>()
        every { imperativeLogger.error(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty { logger.error(exception, marker) { message } }

        assertEquals(marker, markerCaptor.captured)
        assertEquals(exception, exceptionCaptor.captured)
        assertEquals(message, (supplierCaptor.captured)())
    }
    //endregion

    //region Other
    @Test
    fun atLevelMarkerKLoggingEventBuilderSupplier() {
        val message: String = randomText()
        val exception = KLoggerTest.SimulatedException(randomText())
        val level = Level.INFO
        val marker = KMarkerFactory.getMarker(randomText())
        val levelCaptor = slot<Level>()
        val markerCaptor = slot<Marker>()
        val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
        every { imperativeLogger.at(capture(levelCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

        stepVerifierEmpty {
            logger.at(level, marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }
        }

        val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
        assertEquals(message, builder.message)
        assertEquals(exception, builder.cause)
        assertEquals(mapOf("A" to "B"), builder.payload)
        assertEquals(marker, markerCaptor.captured)
        assertEquals(level, levelCaptor.captured)
    }

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
