package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.reactor.MDCSnapshot.Companion.empty
import io.github.numichi.reactive.logger.reactor.MDCSnapshot.Companion.of
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import java.util.*

internal class MDCSnapshotTest {
    companion object {
        fun randomText(): String {
            return UUID.randomUUID().toString()
        }
    }

    @Test
    fun populateAndClear() {
        val expected: MutableMap<String, String> = mutableMapOf()
        expected[randomText()] = randomText()
        expected[randomText()] = randomText()
        assertEquals(mapOf<String, String>(), MDC.getCopyOfContextMap())

        of(expected).use {
            assertEquals(expected, MDC.getCopyOfContextMap())
        }

        assertEquals(mapOf<String, String>(), MDC.getCopyOfContextMap())
    }

    @Test
    fun createEmptyInstance() {
        MDC.put(randomText(), randomText())

        empty().use {
            assertEquals(mapOf<String, String>(), MDC.getCopyOfContextMap())
        }
    }
}