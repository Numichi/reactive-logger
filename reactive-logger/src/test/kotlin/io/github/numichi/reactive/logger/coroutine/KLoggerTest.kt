package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.randomText
import io.github.oshai.kotlinlogging.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class KLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = CoroutineKLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
        clearMocks(imperativeLogger)
    }

    @Test
    fun traceEnabled() {
        every { imperativeLogger.isTraceEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isTraceEnabled)
        assertFalse(logger.isTraceEnabled)
        assertTrue(logger.isTraceEnabled)
    }

    @Test
    fun traceEnabledMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isTraceEnabled(marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isTraceEnabled(marker))
        assertFalse(logger.isTraceEnabled(marker))
        assertTrue(logger.isTraceEnabled(marker))
    }

    @Test
    fun debugEnabled() {
        every { imperativeLogger.isDebugEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isDebugEnabled)
        assertFalse(logger.isDebugEnabled)
        assertTrue(logger.isDebugEnabled)
    }

    @Test
    fun debugEnabledMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isDebugEnabled(marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isDebugEnabled(marker))
        assertFalse(logger.isDebugEnabled(marker))
        assertTrue(logger.isDebugEnabled(marker))
    }

    @Test
    fun infoEnabled() {
        every { imperativeLogger.isInfoEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isInfoEnabled)
        assertFalse(logger.isInfoEnabled)
        assertTrue(logger.isInfoEnabled)
    }

    @Test
    fun infoEnabledMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isInfoEnabled(marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isInfoEnabled(marker))
        assertFalse(logger.isInfoEnabled(marker))
        assertTrue(logger.isInfoEnabled(marker))
    }

    @Test
    fun warnEnabled() {
        every { imperativeLogger.isWarnEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isWarnEnabled)
        assertFalse(logger.isWarnEnabled)
        assertTrue(logger.isWarnEnabled)
    }

    @Test
    fun warnEnabledMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isWarnEnabled(marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isWarnEnabled(marker))
        assertFalse(logger.isWarnEnabled(marker))
        assertTrue(logger.isWarnEnabled(marker))
    }

    @Test
    fun errorEnabled() {
        every { imperativeLogger.isErrorEnabled() } returnsMany listOf(true, false, true)

        assertTrue(logger.isErrorEnabled)
        assertFalse(logger.isErrorEnabled)
        assertTrue(logger.isErrorEnabled)
    }

    @Test
    fun errorEnabledMarker() {
        val marker = KMarkerFactory.getMarker(randomText())
        every { imperativeLogger.isErrorEnabled(marker) } returnsMany listOf(true, false, true)

        assertTrue(logger.isErrorEnabled(marker))
        assertFalse(logger.isErrorEnabled(marker))
        assertTrue(logger.isErrorEnabled(marker))
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
            val exception = SimulatedException(randomText())
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atTrace(capture(supplierCaptor)) } returns Unit

            logger.atTrace {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
        }
    }

    @Test
    fun traceMarkerKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val marker = KMarkerFactory.getMarker(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atTrace(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.atTrace(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
            assertEquals(marker, markerCaptor.captured)
        }
    }

    @Test
    fun traceMessageSupplier() {
        runTest {
            val message: String = randomText()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.trace(capture(supplierCaptor)) } returns Unit

            logger.trace { message }

            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun traceMessageThrowableSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.trace(exception) { message }

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun traceMessageMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.trace(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.trace(marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun traceMessageThrowableMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.trace(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.trace(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(exception, exceptionCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun traceMessage() {
        runTest {
            val message: String = randomText()
            logger.trace(message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.trace(capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun traceMessageThrowable() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.trace(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.trace(message, exception)

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun traceMessageMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()

            logger.trace(marker, message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.trace(null as Throwable?, marker, capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun traceMessageThrowableMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.trace(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.trace(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
            assertEquals(exception, exceptionCaptor.captured)
        }
    }
    //endregion

    //region Debug
    @Test
    fun debugKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atDebug(capture(supplierCaptor)) } returns Unit

            logger.atDebug {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
        }
    }

    @Test
    fun debugMarkerKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val marker = KMarkerFactory.getMarker(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atDebug(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.atDebug(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
            assertEquals(marker, markerCaptor.captured)
        }
    }

    @Test
    fun debugMessageSupplier() {
        runTest {
            val message: String = randomText()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.debug(capture(supplierCaptor)) } returns Unit

            logger.debug { message }

            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun debugMessageThrowableSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.debug(exception) { message }

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun debugMessageMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.debug(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.debug(marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun debugMessageThrowableMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.debug(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.debug(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(exception, exceptionCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun debugMessage() {
        runTest {
            val message: String = randomText()
            logger.debug(message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.debug(capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun debugMessageThrowable() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.debug(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.debug(message, exception)

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun debugMessageMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()

            logger.debug(marker, message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.debug(null as Throwable?, marker, capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun debugMessageThrowableMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.debug(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.debug(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
            assertEquals(exception, exceptionCaptor.captured)
        }
    }
    //endregion

    //region Info
    @Test
    fun infoKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atInfo(capture(supplierCaptor)) } returns Unit

            logger.atInfo {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
        }
    }

    @Test
    fun infoMarkerKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val marker = KMarkerFactory.getMarker(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atInfo(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.atInfo(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
            assertEquals(marker, markerCaptor.captured)
        }
    }

    @Test
    fun infoMessageSupplier() {
        runTest {
            val message: String = randomText()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.info(capture(supplierCaptor)) } returns Unit

            logger.info { message }

            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun infoMessageThrowableSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.info(exception) { message }

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun infoMessageMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.info(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.info(marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun infoMessageThrowableMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.info(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.info(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(exception, exceptionCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun infoMessage() {
        runTest {
            val message: String = randomText()
            logger.info(message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.info(capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun infoMessageThrowable() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.info(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.info(message, exception)

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun infoMessageMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()

            logger.info(marker, message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.info(null as Throwable?, marker, capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun infoMessageThrowableMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.info(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.info(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
            assertEquals(exception, exceptionCaptor.captured)
        }
    }
    //endregion

    //region Warn
    @Test
    fun warnKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atWarn(capture(supplierCaptor)) } returns Unit

            logger.atWarn {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
        }
    }

    @Test
    fun warnMarkerKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val marker = KMarkerFactory.getMarker(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atWarn(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.atWarn(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
            assertEquals(marker, markerCaptor.captured)
        }
    }

    @Test
    fun warnMessageSupplier() {
        runTest {
            val message: String = randomText()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.warn(capture(supplierCaptor)) } returns Unit

            logger.warn { message }

            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun warnMessageThrowableSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.warn(exception) { message }

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun warnMessageMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.warn(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.warn(marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun warnMessageThrowableMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.warn(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.warn(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(exception, exceptionCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun warnMessage() {
        runTest {
            val message: String = randomText()
            logger.warn(message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.warn(capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun warnMessageThrowable() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.warn(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.warn(message, exception)

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun warnMessageMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()

            logger.warn(marker, message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.warn(null as Throwable?, marker, capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun warnMessageThrowableMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.warn(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.warn(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
            assertEquals(exception, exceptionCaptor.captured)
        }
    }
    //endregion

    //region Error
    @Test
    fun errorKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atError(capture(supplierCaptor)) } returns Unit

            logger.atError {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
        }
    }

    @Test
    fun errorMarkerKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val marker = KMarkerFactory.getMarker(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.atError(capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.atError(marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
            assertEquals(marker, markerCaptor.captured)
        }
    }

    @Test
    fun errorMessageSupplier() {
        runTest {
            val message: String = randomText()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.error(capture(supplierCaptor)) } returns Unit

            logger.error { message }

            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun errorMessageThrowableSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.error(exception) { message }

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun errorMessageMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.error(null as Throwable?, capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.error(marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun errorMessageThrowableMarkerSupplier() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.error(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.error(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(exception, exceptionCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun errorMessage() {
        runTest {
            val message: String = randomText()
            logger.error(message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.error(capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun errorMessageThrowable() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val exceptionCaptor = slot<SimulatedException>()
            val supplierCaptor = slot<() -> Any?>()
            every { imperativeLogger.error(capture(exceptionCaptor), capture(supplierCaptor)) } returns Unit

            logger.error(message, exception)

            assertEquals(exceptionCaptor.captured.message, exception.message)
            assertEquals(message, (supplierCaptor.captured)())
        }
    }

    @Test
    fun errorMessageMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()

            logger.error(marker, message)

            val stringCaptor = slot<() -> Any?>()
            verify { imperativeLogger.error(null as Throwable?, marker, capture(stringCaptor)) }
            assertEquals(message, stringCaptor.captured())
        }
    }

    @Test
    fun errorMessageThrowableMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<() -> Any?>()
            val exceptionCaptor = slot<SimulatedException>()
            every { imperativeLogger.error(capture(exceptionCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.error(exception, marker) { message }

            assertEquals(marker, markerCaptor.captured)
            assertEquals(message, (supplierCaptor.captured)())
            assertEquals(exception, exceptionCaptor.captured)
        }
    }
    //endregion

    //region Other
    @Test
    fun atLevelMarkerKLoggingEventBuilderSupplier() {
        runTest {
            val message: String = randomText()
            val exception = SimulatedException(randomText())
            val level = Level.INFO
            val marker = KMarkerFactory.getMarker(randomText())
            val levelCaptor = slot<Level>()
            val markerCaptor = slot<Marker>()
            val supplierCaptor = slot<KLoggingEventBuilder.() -> Unit>()
            every { imperativeLogger.at(capture(levelCaptor), capture(markerCaptor), capture(supplierCaptor)) } returns Unit

            logger.at(level, marker) {
                this.message = message
                this.cause = exception
                this.payload = mapOf("A" to "B")
            }

            val builder = KLoggingEventBuilder().apply(supplierCaptor.captured)
            assertEquals(message, builder.message)
            assertEquals(exception, builder.cause)
            assertEquals(mapOf("A" to "B"), builder.payload)
            assertEquals(marker, markerCaptor.captured)
            assertEquals(level, levelCaptor.captured)
        }
    }

    @Test
    fun entry() {
        runTest {
            val message1: String = randomText()
            val message2: String = randomText()
            logger.entry(message1, message2)

            verify { imperativeLogger.entry(message1, message2) }
        }
    }

    @Test
    fun exit() {
        runTest {
            logger.exit()

            verify { imperativeLogger.exit() }
        }
    }

    @Test
    fun exitParameterized() {
        runTest {
            val message: String = randomText()
            val result = logger.exit(message)
            verify { imperativeLogger.exit(message) }

            assertEquals(result, message)
        }
    }

    @Test
    fun throwing() {
        runTest {
            val message: String = randomText()
            val result = logger.throwing(SimulatedException(message))

            assertEquals(message, result.message)
        }
    }

    @Test
    fun catching() {
        runTest {
            val message: String = randomText()
            logger.catching(SimulatedException(message))
        }
    }
    //endregion

    class SimulatedException(message: String?) : RuntimeException(message)
}
