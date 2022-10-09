package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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

        run {
            val instance1 = ReactiveLogger.getLogger(logger)
            val instance2 = ReactiveLogger.getLogger(logger, "foo")
            val instance3 = ReactiveLogger.getLogger(logger, Schedulers.parallel())
            val instance4 = ReactiveLogger.getLogger(logger, "foo", Schedulers.parallel())
            val instance5 = ReactiveLogger.getLogger(logger, null, null)

            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance4.logger.name)

            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance1.contextKey)
            assertEquals("foo", instance2.contextKey)
            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance3.contextKey)
            assertEquals("foo", instance4.contextKey)
            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance5.contextKey)

            assertSame(Schedulers.boundedElastic(), instance1.scheduler)
            assertSame(Schedulers.boundedElastic(), instance2.scheduler)
            assertSame(Schedulers.parallel(), instance3.scheduler)
            assertSame(Schedulers.parallel(), instance4.scheduler)
            assertSame(Schedulers.boundedElastic(), instance5.scheduler)
        }

        run {
            val instance1 = ReactiveLogger.getLogger("bar")
            val instance2 = ReactiveLogger.getLogger("bar", "foo")
            val instance3 = ReactiveLogger.getLogger("bar", Schedulers.parallel())
            val instance4 = ReactiveLogger.getLogger("bar", "foo", Schedulers.parallel())
            val instance5 = ReactiveLogger.getLogger("bar", null, null)

            assertEquals("bar", instance1.logger.name)
            assertEquals("bar", instance2.logger.name)
            assertEquals("bar", instance3.logger.name)
            assertEquals("bar", instance4.logger.name)
            assertEquals("bar", instance5.logger.name)

            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance1.contextKey)
            assertEquals("foo", instance2.contextKey)
            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance3.contextKey)
            assertEquals("foo", instance4.contextKey)
            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance5.contextKey)

            assertSame(Schedulers.boundedElastic(), instance1.scheduler)
            assertSame(Schedulers.boundedElastic(), instance2.scheduler)
            assertSame(Schedulers.parallel(), instance3.scheduler)
            assertSame(Schedulers.parallel(), instance4.scheduler)
            assertSame(Schedulers.boundedElastic(), instance5.scheduler)
        }

        run {
            val instance1 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java)
            val instance2 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java, "foo")
            val instance3 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java, Schedulers.parallel())
            val instance4 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java, "foo", Schedulers.parallel())
            val instance5 = ReactiveLogger.getLogger(ReactiveLoggerTest::class.java, null, null)

            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLoggerTest", instance5.logger.name)

            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance1.contextKey)
            assertEquals("foo", instance2.contextKey)
            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance3.contextKey)
            assertEquals("foo", instance4.contextKey)
            assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, instance5.contextKey)

            assertSame(Schedulers.boundedElastic(), instance1.scheduler)
            assertSame(Schedulers.boundedElastic(), instance2.scheduler)
            assertSame(Schedulers.parallel(), instance3.scheduler)
            assertSame(Schedulers.parallel(), instance4.scheduler)
            assertSame(Schedulers.boundedElastic(), instance5.scheduler)
        }
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
        val loggerWithCustomScheduler = ReactiveLogger.getLogger(imperativeLogger, contextKey = contextKey)
        assertSame(loggerWithCustomScheduler.contextKey, contextKey)
    }

    @Test
    fun schedulerTest() {
        val customScheduler = Schedulers.newBoundedElastic(10, 10, randomText())
        val loggerWithCustomScheduler = ReactiveLogger.getLogger(imperativeLogger, scheduler = customScheduler)
        assertSame(loggerWithCustomScheduler.scheduler, customScheduler)
    }
}