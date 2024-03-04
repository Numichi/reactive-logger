package io.github.numichi.reactive.logger.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

internal class LoggerFactoryTest {

    @Test
    fun getLogger() {
        val instance1 = LoggerFactory.getLogger(this::class.java)
        val instance2 = LoggerFactory.getLogger("test")

        assertNotNull(instance1)
        assertNotNull(instance2)

        assertEquals(instance1.name, "io.github.numichi.reactive.logger.internal.LoggerFactoryTest")
        assertEquals(instance2.name, "test")
    }


    @Test
    fun getKLogger() {
        val instance1 = LoggerFactory.getKLogger(this::class.java)
        val instance3 = LoggerFactory.getKLogger("test")
        val instance4 = LoggerFactory.getKLogger {}

        assertNotNull(instance1)
        assertNotNull(instance3)
        assertNotNull(instance4)

        assertEquals(instance1.name, "io.github.numichi.reactive.logger.internal.LoggerFactoryTest")
        assertEquals(instance3.name, "test")
        assertEquals(instance4.name, "io.github.numichi.reactive.logger.internal.LoggerFactoryTest")
    }
}