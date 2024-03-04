package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.randomText
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KMarkerFactory
import io.github.oshai.kotlinlogging.Marker
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
        assertFalse(logger.isTraceEnabled, "trace enabled when it should not be")
        assertTrue(logger.isTraceEnabled, "trace not enabled when it should be")
    }

    @Test
    fun traceEnabledMarker() {
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            every { imperativeLogger.isTraceEnabled(marker) } returnsMany listOf(true, false, true)

            assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
            assertFalse(logger.isTraceEnabled(marker), "trace enabled when it should not be")
            assertTrue(logger.isTraceEnabled(marker), "trace not enabled when it should be")
        }
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
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            every { imperativeLogger.isDebugEnabled(marker) } returnsMany listOf(true, false, true)

            assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
            assertFalse(logger.isDebugEnabled(marker), "debug enabled when it should not be")
            assertTrue(logger.isDebugEnabled(marker), "debug not enabled when it should be")
        }
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
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            every { imperativeLogger.isInfoEnabled(marker) } returnsMany listOf(true, false, true)

            assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
            assertFalse(logger.isInfoEnabled(marker), "info enabled when it should not be")
            assertTrue(logger.isInfoEnabled(marker), "info not enabled when it should be")
        }
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
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            every { imperativeLogger.isWarnEnabled(marker) } returnsMany listOf(true, false, true)

            assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
            assertFalse(logger.isWarnEnabled(marker), "warn enabled when it should not be")
            assertTrue(logger.isWarnEnabled(marker), "warn not enabled when it should be")
        }
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
        runTest {
            val marker = KMarkerFactory.getMarker(randomText())
            every { imperativeLogger.isErrorEnabled(marker) } returnsMany listOf(true, false, true)

            assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
            assertFalse(logger.isErrorEnabled(marker), "error enabled when it should not be")
            assertTrue(logger.isErrorEnabled(marker), "error not enabled when it should be")
        }
    }

    //region Trace
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