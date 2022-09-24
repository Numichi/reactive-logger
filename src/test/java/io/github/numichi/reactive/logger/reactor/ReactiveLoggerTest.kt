package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Schedulers

@ExperimentalCoroutinesApi
internal class ReactiveLoggerTest {
    private val imperativeLogger: Logger = mockk(relaxed = true)
    private val logger = ReactiveLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun creationTest() {
        val logger = LoggerFactory.getLogger(this::class.java)
        val klogger = KotlinLogging.logger(logger)

        val i1 = ReactiveLogger.getLogger(logger)
        val i2 = ReactiveLogger.getLogger(logger, "foo", Schedulers.single())
        val i3 = ReactiveLogger.getLogger(klogger)
        val i4 = ReactiveLogger.getLogger(klogger, "foo", Schedulers.single())
        val i7 = ReactiveLogger.getLogger("foobar")
        val i8 = ReactiveLogger.getLogger("foobar", "foo", Schedulers.single())
        val i9 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java)
        val i10 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java, "foo", Schedulers.single())

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", i1.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i1.contextKey)
        assertSame(Schedulers.boundedElastic(), i1.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", i2.logger.name)
        assertEquals("foo", i2.contextKey)
        assertSame(Schedulers.single(), i2.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", i3.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i3.contextKey)
        assertSame(Schedulers.boundedElastic(), i3.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", i4.logger.name)
        assertEquals("foo", i4.contextKey)
        assertSame(Schedulers.single(), i4.scheduler)

        assertEquals("foobar", i7.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i7.contextKey)
        assertSame(Schedulers.boundedElastic(), i7.scheduler)

        assertEquals("foobar", i8.logger.name)
        assertEquals("foo", i8.contextKey)
        assertSame(Schedulers.single(), i8.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", i9.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i9.contextKey)
        assertSame(Schedulers.boundedElastic(), i9.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", i10.logger.name)
        assertEquals("foo", i10.contextKey)
        assertSame(Schedulers.single(), i10.scheduler)
    }

    @Test
    fun nameTest() {
        val name = randomText()
        every { imperativeLogger.name } returns name
        assertEquals(name, logger.name)
        assertEquals(name, logger.logger.name)
    }

    @Test
    fun imperative() {
        assertSame(logger.logger, imperativeLogger)
    }

    @Test
    fun mdcContextKexTest() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = ReactiveLogger.getLogger(imperativeLogger, contextKey = contextKey)
        assertSame(loggerWithCustomScheduler.contextKey, contextKey)

        assertThrows<IllegalStateException> {
            ReactiveLogger.getLogger(imperativeLogger, contextKey = "")
        }

        assertThrows<IllegalStateException> {
            ReactiveLogger.getLogger(imperativeLogger, contextKey = " ")
        }
    }

    @Test
    fun schedulerTest() {
        val customScheduler = Schedulers.newBoundedElastic(10, 10, randomText())
        val loggerWithCustomScheduler = ReactiveLogger.getLogger(imperativeLogger, scheduler = customScheduler)
        assertSame(loggerWithCustomScheduler.scheduler, customScheduler)
    }
}