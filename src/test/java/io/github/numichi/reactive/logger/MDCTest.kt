package io.github.numichi.reactive.logger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.time.Instant

internal class MDCTest {
    @Test
    fun contextKeyTesT() {
        val mdc = MDC()
        assertEquals(mdc.contextKey, mdc.contextKey)
    }

    @Test
    fun testEquals() {
        val mdc1 = MDC()
        mdc1["111"] = "222"

        val mdc2 = MDC()
        mdc2["111"] = "222"

        val mdc3 = MDC()
        mdc3["333"] = "444"

        assertEquals(mdc1, mdc1)
        assertEquals(mdc1, mdc2)
        assertNotEquals(mdc1, mdc3)
        assertFalse(mdc1.equals(Instant.now()))
    }

    @Test
    fun testHashCode() {
        val mdc1 = MDC()
        mdc1["111"] = "222"
        val mdc2 = MDC()
        mdc2["111"] = "222"

        assertEquals(mdc1.hashCode(), mdc2.hashCode())
    }
}