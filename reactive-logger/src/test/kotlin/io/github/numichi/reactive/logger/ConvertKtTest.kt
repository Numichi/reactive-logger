package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.oshai.kotlinlogging.KMarkerFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import org.slf4j.event.Level
import io.github.oshai.kotlinlogging.Level as KLevel
import io.github.oshai.kotlinlogging.Marker as KMarker

class ConvertKtTest {
    @Test
    fun `Kotlin Marker toKMarker returns equivalent Slf4j Marker`() {
        val expected: KMarker = KMarkerFactory.getMarker("TestMarker")
        val actual: Marker = expected.toSlf4j()

        assertEquals(expected.getName(), actual.name)
        assertEquals(expected, actual.toKMarker())
    }

    @Test
    fun `Slf4j Marker toSlf4j returns equivalent Kotlin Marker`() {
        val expected: Marker = MarkerFactory.getMarker("TestMarker")
        val actual: KMarker = expected.toKMarker()

        assertEquals(expected.name, actual.getName())
        assertEquals(expected, actual.toSlf4j())
    }

    @ParameterizedTest
    @ValueSource(strings = ["TRACE", "DEBUG", "INFO", "WARN", "ERROR"])
    fun `Kotlin Level toKMarker returns equivalent Slf4j Level`(level: String) {
        val expected: Level = Level.valueOf(level)
        val actual: KLevel = expected.toKLevel()

        assertEquals(expected.name, actual.name)
        assertEquals(expected, actual.toSlf4j())

        assertEquals(KLevel.OFF, expected.toKLevel(true))
    }

    @Test
    fun `Slf4j Level toSlf4j returns equivalent Kotlin Level`() {
        val expected: KLevel = KLevel.INFO
        val actual: Level = expected.toSlf4j()

        assertEquals(expected.name, actual.name)
        assertEquals(expected, actual.toKLevel())
    }

    @Test
    fun `Kotlin OFF Level can not be convert to Slf4j Level`() {
        assertThrows<IllegalArgumentException> {
            KLevel.OFF.toSlf4j()
        }
    }

    @Test
    fun `Kotlin Logger toSlf4j returns equivalent Slf4j Logger`() {
        val kLogger1 = ReactiveKLogger.getLogger("TestLogger1")
        val kLogger2 = CoroutineKLogger.getLogger("TestLogger2")

        val logger1 = ReactiveLogger.getLogger(kLogger1.logger)
        val logger2 = CoroutineLogger.getLogger(kLogger1.logger)
        val logger3 = ReactiveLogger.getLogger(kLogger2.logger)
        val logger4 = CoroutineLogger.getLogger(kLogger2.logger)

        assertEquals(logger1.logger, kLogger1.logger.toSlf4j())
        assertEquals(logger2.logger, kLogger1.logger.toSlf4j())
        assertEquals(logger1.logger, kLogger1.logger.toSlf4j())
        assertEquals(logger2.logger, kLogger1.logger.toSlf4j())

        assertEquals(logger3.logger, kLogger2.logger.toSlf4j())
        assertEquals(logger4.logger, kLogger2.logger.toSlf4j())
        assertEquals(logger3.logger, kLogger2.logger.toSlf4j())
        assertEquals(logger4.logger, kLogger2.logger.toSlf4j())
    }

    @Test
    fun `Slf4j Logger toSlf4j returns same (!) Kotlin Logger`() {
        val kLogger1 = ReactiveKLogger.getLogger("TestLogger1")
        val kLogger2 = CoroutineKLogger.getLogger("TestLogger2")

        val logger1 = ReactiveLogger.getLogger(kLogger1.logger)
        val logger2 = CoroutineLogger.getLogger(kLogger1.logger)
        val logger3 = ReactiveLogger.getLogger(kLogger2.logger)
        val logger4 = CoroutineLogger.getLogger(kLogger2.logger)

        assertNotEquals(kLogger1.logger, logger1.logger.toKLogger())
        assertNotEquals(kLogger1.logger, logger2.logger.toKLogger())
        assertNotEquals(kLogger2.logger, logger3.logger.toKLogger())
        assertNotEquals(kLogger2.logger, logger4.logger.toKLogger())

        assertEquals(logger1.logger.name,  kLogger1.logger.toSlf4j().name)
        assertEquals(logger2.logger.name,  kLogger1.logger.toSlf4j().name)
        assertEquals(logger1.logger.name,  kLogger1.logger.toSlf4j().name)
        assertEquals(logger2.logger.name,  kLogger1.logger.toSlf4j().name)

        assertEquals(logger3.logger.name,  kLogger2.logger.toSlf4j().name)
        assertEquals(logger4.logger.name,  kLogger2.logger.toSlf4j().name)
        assertEquals(logger3.logger.name,  kLogger2.logger.toSlf4j().name)
        assertEquals(logger4.logger.name,  kLogger2.logger.toSlf4j().name)
    }
}