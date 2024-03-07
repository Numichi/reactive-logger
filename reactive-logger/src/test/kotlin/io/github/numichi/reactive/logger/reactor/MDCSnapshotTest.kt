package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.randomText
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.MDC

internal class MDCSnapshotTest {
    @Test
    fun populateAndClear() {
        val expected: MutableMap<String, String?> = mutableMapOf()
        expected[randomText()] = randomText()
        expected[randomText()] = randomText()
        assertEquals(mapOf<String, String?>(), MDC.getCopyOfContextMap())

        MDCSnapshot.of(expected).use {
            assertEquals(expected, MDC.getCopyOfContextMap())
            assertEquals(expected, it.copyOfContextMap)
        }

        assertEquals(mapOf<String, String?>(), MDC.getCopyOfContextMap())
    }

    @Test
    fun createEmptyInstance() {
        MDC.put(randomText(), randomText())

        MDCSnapshot.empty().use {
            assertEquals(mapOf<String, String?>(), it.copyOfContextMap)
            assertEquals(mapOf<String, String?>(), MDC.getCopyOfContextMap())
        }
    }
}
