package io.github.numichi.reactive.logger.example.kotlin

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.exceptions.ReadException
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger

@ExperimentalCoroutinesApi
internal class ExampleComponentTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val component = ExampleComponent(logger)

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
        clearMocks(logger)
    }

    @Test
    fun example1Test() = runTest {
        val result = component.example1()

        assertEquals(mapOf("foo" to "bar"), result)

        verify(exactly = 1) { logger.info("example1") }
    }

    @Test
    fun example2Test() = runTest {
        component.example2()

        verify(exactly = 0) { logger.trace(any()) }
        verify(exactly = 2) { logger.debug("bar") }
        verify(exactly = 1) { logger.info("null") }
        verify(exactly = 0) { logger.warn(any()) }
    }

    @Test
    fun example3Test() = runTest {
        component.example3()

        verify(exactly = 0) { logger.trace(any()) }
        verify(exactly = 2) { logger.debug("bar") }
        verify(exactly = 1) { logger.info("null") }
        verify(exactly = 0) { logger.warn(any()) }
    }

    @Test
    fun example4Test() = runTest {
        val throwable = assertThrows<ReadException> {
            component.example4()
        }

        val expected = "DEFAULT_REACTOR_CONTEXT_MDC_KEY context key is not contain in context"

        assertEquals(expected, throwable.message)
        verify(exactly = 1) { logger.error(expected) }
    }
}