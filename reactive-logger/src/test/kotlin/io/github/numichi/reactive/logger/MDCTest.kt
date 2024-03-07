package io.github.numichi.reactive.logger

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.util.function.Tuples

internal class MDCTest {
    lateinit var a: String

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
    }

    @Test
    fun asd() {
        try {
            assertEquals("", this.a)
        } catch (e: UninitializedPropertyAccessException) {
            assertTrue(true)
        }
    }

    @Test
    fun constructorTest() {
        val mdc1 = MDC(mapOf("111" to "222"))
        val mdc2 = MDC("111" to "222")
        val mdc3 = MDC(arrayOf("111" to "222"))
        val mdc4 = MDC(Tuples.of("111", "222"))
        val mdc5 = MDC(arrayOf(Tuples.of("111", "222")))

        assertEquals(mdc1, mdc2)
        assertEquals(mdc2, mdc3)
        assertEquals(mdc3, mdc4)
        assertEquals(mdc4, mdc5)

        val mdc6 = MDC("foo", mapOf("111" to "222"))
        val mdc7 = MDC("foo", "111" to "222")
        val mdc8 = MDC("foo", arrayOf("111" to "222"))
        val mdc9 = MDC("foo", Tuples.of("111", "222"))
        val mdc10 = MDC("foo", arrayOf(Tuples.of("111", "222")))

        assertEquals(mdc6, mdc7)
        assertEquals(mdc7, mdc8)
        assertEquals(mdc8, mdc9)
        assertEquals(mdc9, mdc10)

        val mdc11 = MDC(mdc10)

        assertEquals(mdc11, mdc10)
        assertEquals(mdc11.hashCode(), mdc10.hashCode())
    }

    @Test
    fun defaultValuesTest() {
        val mdc1 = MDC()
        val mdc2 = MDC()

        assertEquals(mdc1.contextKey, Configuration.defaultReactorContextMdcKey)
        assertEquals(mdc1.contextKey, mdc2.contextKey)
        assertEquals(mapOf<String, String?>(), mdc1.data)
        assertEquals(mapOf<String, String?>(), mdc2.data)
    }

    @Test
    fun equalsTest() {
        val mdc1 = MDC("111" to "222")
        val mdc2 = MDC("111" to "222")
        val mdc3 = MDC("333" to "444")

        assertEquals(mdc1, mdc1)
        assertEquals(mdc1, mdc2)
        assertNotEquals(mdc1, mdc3)
    }

    @Test
    fun minusTest() {
        val mdc = MDC(mapOf("111" to "222", "333" to "444", "555" to "666"))
        val minus = MDC("333" to "444")

        val expected = MDC(mapOf("111" to "222", "555" to "666"))
        assertEquals(expected, mdc - minus)
        assertEquals(expected, mdc.minus(minus))

        assertEquals(expected, mdc - arrayOf("333"))
        assertEquals(expected, mdc - setOf("333"))
        assertEquals(expected, mdc - "333")
    }

    @Test
    fun sizeTest() {
        val mdc0 = MDC()
        val mdc2 = MDC(mapOf("111" to "222", "333" to "444"))

        assertEquals(0, mdc0.size)
        assertEquals(2, mdc2.size)
    }

    @Test
    fun getTest() {
        val mdc1 = MDC("111" to "222")

        assertEquals(null, mdc1["000"])
        assertEquals("222", mdc1["111"])
    }

    @Test
    fun plusTest() {
        val mdc1 = MDC("111" to "222")
        val mdc2 = MDC("333" to "444")

        val expected = MDC(mapOf("111" to "222", "333" to "444"))

        assertEquals(expected, mdc1 + mdc2)
        assertEquals(expected, mdc1.plus(mdc2))

        assertEquals(expected, mdc1 + ("333" to "444"))
        assertEquals(expected, mdc1 + arrayOf("333" to "444"))
        assertEquals(expected, mdc1 + mapOf("333" to "444"))
        assertEquals(expected, mdc1 + Tuples.of("333", "444"))
        assertEquals(expected, mdc1 + arrayOf(Tuples.of("333", "444")))
    }

    @Test
    fun keepTest() {
        val mdc = MDC(mapOf("111" to "222", "333" to "444", "555" to "666"))

        val expected0 = MDC()
        assertEquals(expected0, mdc.keep("000"))

        val expected1 = MDC(mapOf("111" to "222"))
        assertEquals(expected1, mdc.keep("111"))

        val expected2 = MDC(mapOf("111" to "222", "333" to "444"))
        assertEquals(expected2, mdc.keep(setOf("111", "777", "333")))
        assertEquals(expected2, mdc.keep(arrayOf("111", "777", "333")))
        assertEquals(expected2, mdc.keep(MDC(mapOf("111" to "222", "777" to "888", "333" to "xxx"))))
    }

    @Test
    fun cleanTest() {
        val mdc1 = MDC("foo", mapOf("111" to "222"))
        val mdc2 = MDC("foo", mapOf())

        assertEquals(mdc2, mdc1.clean())
    }

    @Test
    fun throwsTest() {
        val mdc1 = MDC(Tuples.of("111", "222"))
        val mdc2 = MDC("foo", mapOf("111" to "222"))

        assertThrows<IllegalStateException> { mdc1 + mdc2 }
        assertThrows<IllegalStateException> { mdc1 - mdc2 }
        assertThrows<IllegalStateException> { mdc1.keep(mdc2) }
    }
}
