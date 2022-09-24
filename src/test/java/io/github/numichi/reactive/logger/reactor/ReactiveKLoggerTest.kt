package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import mu.KLogger
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Schedulers

@ExperimentalCoroutinesApi
internal class ReactiveKLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = ReactiveKLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun creationTest() {
        val logger = LoggerFactory.getLogger(this::class.java)
        val klogger = KotlinLogging.logger(logger)

        val i1 = ReactiveKLogger.getLogger(logger)
        val i2 = ReactiveKLogger.getLogger(logger, "foo", Schedulers.single())
        val i3 = ReactiveKLogger.getLogger(klogger)
        val i4 = ReactiveKLogger.getLogger(klogger, "foo", Schedulers.single())
        val i5 = ReactiveKLogger.getLogger {}
        val i6 = ReactiveKLogger.getLogger({}, "foo", Schedulers.single())
        val i7 = ReactiveKLogger.getLogger("foobar")
        val i8 = ReactiveKLogger.getLogger("foobar", "foo", Schedulers.single())
        val i9 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java)
        val i10 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java, "foo", Schedulers.single())

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i1.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i1.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i1.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i2.logger.underlyingLogger.name)
        assertEquals("foo", i2.mdcContextKey)
        assertSame(Schedulers.single(), i2.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i3.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i3.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i3.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i4.logger.underlyingLogger.name)
        assertEquals("foo", i4.mdcContextKey)
        assertSame(Schedulers.single(), i4.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i5.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i5.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i5.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i6.logger.underlyingLogger.name)
        assertEquals("foo", i6.mdcContextKey)
        assertSame(Schedulers.single(), i6.scheduler)

        assertEquals("foobar", i7.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i7.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i7.scheduler)

        assertEquals("foobar", i8.logger.underlyingLogger.name)
        assertEquals("foo", i8.mdcContextKey)
        assertSame(Schedulers.single(), i8.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i9.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i9.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i9.scheduler)

        assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", i10.logger.underlyingLogger.name)
        assertEquals("foo", i10.mdcContextKey)
        assertSame(Schedulers.single(), i10.scheduler)
    }

    @Test
    fun nameTest() {
        val name = randomText()
        every { imperativeLogger.name } returns name
        assertEquals(name, logger.logger.name)
    }

    @Test
    fun imperative() {
        assertSame(logger.logger, imperativeLogger)
    }

    @Test
    fun mdcContextKexTest() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = ReactiveKLogger.getLogger(imperativeLogger, mdcContextKey = contextKey)
        assertSame(loggerWithCustomScheduler.mdcContextKey, contextKey)

        assertThrows<IllegalStateException> {
            ReactiveKLogger.getLogger(imperativeLogger, mdcContextKey = "")
        }

        assertThrows<IllegalStateException> {
            ReactiveKLogger.getLogger(imperativeLogger, mdcContextKey = " ")
        }
    }

    @Test
    fun scheduler() {
        val customScheduler = Schedulers.newBoundedElastic(10, 10, randomText())
        val loggerWithCustomScheduler = ReactiveKLogger.getLogger(imperativeLogger, scheduler = customScheduler)
        assertSame(loggerWithCustomScheduler.scheduler, customScheduler)
    }
}