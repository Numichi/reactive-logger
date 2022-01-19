package hu.numichi.kotlin.reactive.logger

import hu.numichi.reactive.logger.exception.InvalidContextDataException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import reactor.util.context.Context

internal class MDCTest {
    @Test
    fun test() {
        assertThrows(InvalidContextDataException::class.java) { MDC.restore(Context.of("key", "value"), "key") }

        assertEquals(mutableMapOf<String, String>(), MDC.restore(Context.of("key", "value"), "other").mdcMap)
    }
}