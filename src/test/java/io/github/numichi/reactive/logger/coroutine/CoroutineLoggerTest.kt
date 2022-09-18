package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Schedulers

@ExperimentalCoroutinesApi
internal class CoroutineLoggerTest {
    private val imperativeLogger: Logger = mockk(relaxed = true)
    private val logger = CoroutineLogger.getLogger(imperativeLogger)
    private val loggerScheduled = CoroutineLogger.getLogger(imperativeLogger, scheduler = Schedulers.parallel())

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
        clearMocks(imperativeLogger)
    }

    @Test
    fun createReactiveLogger() {
        runTest {
            val instance1 = CoroutineLogger.getLogger(imperativeLogger)
            assertNotNull(instance1)
            assertEquals(Configuration.defaultReactorContextMdcKey, instance1.mdcContextKey)
        }
    }

    @Test
    fun creationTest() {
        val logger = LoggerFactory.getLogger(this::class.java)
        val klogger = KotlinLogging.logger(logger)

        val i1 = CoroutineLogger.getLogger(logger)
        val i2 = CoroutineLogger.getLogger(logger, "foo", Schedulers.single())
        val i3 = CoroutineLogger.getLogger(klogger)
        val i4 = CoroutineLogger.getLogger(klogger, "foo", Schedulers.single())
        val i7 = CoroutineLogger.getLogger("foobar")
        val i8 = CoroutineLogger.getLogger("foobar", "foo", Schedulers.single())
        val i9 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java)
        val i10 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java, "foo", Schedulers.single())

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", i1.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i1.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i1.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", i2.logger.name)
        assertEquals("foo", i2.mdcContextKey)
        assertSame(Schedulers.single(), i2.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", i3.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i3.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i3.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", i4.logger.name)
        assertEquals("foo", i4.mdcContextKey)
        assertSame(Schedulers.single(), i4.scheduler)

        assertEquals("foobar", i7.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i7.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i7.scheduler)

        assertEquals("foobar", i8.logger.name)
        assertEquals("foo", i8.mdcContextKey)
        assertSame(Schedulers.single(), i8.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", i9.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i9.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i9.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", i10.logger.name)
        assertEquals("foo", i10.mdcContextKey)
        assertSame(Schedulers.single(), i10.scheduler)
    }

    @Test
    fun getName() {
        val name: String = randomText()
        every { imperativeLogger.name } returns name
        assertEquals(name, logger.name)
    }

    @Test
    fun imperative() {
        assertSame(logger.reactorLogger.logger, imperativeLogger)
    }

    @Test
    fun slf4jLogger() {
        assertSame(logger.reactorLogger.slf4jLogger, imperativeLogger)
    }

    @Test
    fun scheduled() {
        assertSame(Schedulers.boundedElastic(), logger.scheduler)
        assertSame(Schedulers.parallel(), loggerScheduled.scheduler)
    }

    @Test
    fun contextKey() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = CoroutineLogger.getLogger(imperativeLogger, mdcContextKey = contextKey)
        assertSame(loggerWithCustomScheduler.mdcContextKey, contextKey)

        assertThrows<IllegalStateException> {
            CoroutineLogger.getLogger(imperativeLogger, mdcContextKey = "")
        }

        assertThrows<IllegalStateException> {
            CoroutineLogger.getLogger(imperativeLogger, mdcContextKey = " ")
        }
    }
}