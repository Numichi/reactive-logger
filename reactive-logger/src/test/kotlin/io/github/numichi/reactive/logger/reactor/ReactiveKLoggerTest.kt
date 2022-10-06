package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE
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

        run {
            val instance1 = ReactiveKLogger.getLogger(logger)
            val instance2 = ReactiveKLogger.getLogger(logger, "foo")
            val instance3 = ReactiveKLogger.getLogger(logger, Schedulers.parallel())
            val instance4 = ReactiveKLogger.getLogger(logger, "foo", Schedulers.parallel())
            val instance5 = ReactiveKLogger.getLogger(logger, null, null)

            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance4.logger.name)

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
            val instance1 = ReactiveKLogger.getLogger(klogger)
            val instance2 = ReactiveKLogger.getLogger(klogger, "foo")
            val instance3 = ReactiveKLogger.getLogger(klogger, Schedulers.parallel())
            val instance4 = ReactiveKLogger.getLogger(klogger, "foo", Schedulers.parallel())
            val instance5 = ReactiveKLogger.getLogger(klogger, null, null)

            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance5.logger.name)

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
            val instance1 = ReactiveKLogger.getLogger {}
            val instance2 = ReactiveKLogger.getLogger("foo") {}
            val instance3 = ReactiveKLogger.getLogger(Schedulers.parallel()) {}
            val instance4 = ReactiveKLogger.getLogger("foo", Schedulers.parallel()) {}
            val instance5 = ReactiveKLogger.getLogger(null, null) {}

            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance5.logger.name)

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
            val instance1 = ReactiveKLogger.getLogger("bar")
            val instance2 = ReactiveKLogger.getLogger("bar", "foo")
            val instance3 = ReactiveKLogger.getLogger("bar", Schedulers.parallel())
            val instance4 = ReactiveKLogger.getLogger("bar", "foo", Schedulers.parallel())
            val instance5 = ReactiveKLogger.getLogger("bar", null, null)

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
            val instance1 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java)
            val instance2 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java, "foo")
            val instance3 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java, Schedulers.parallel())
            val instance4 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java, "foo", Schedulers.parallel())
            val instance5 = ReactiveKLogger.getLogger(ReactiveKLoggerTest::class.java, null, null)

            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLoggerTest", instance5.logger.name)

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
            val error1 = assertThrows<IllegalStateException> { ReactiveKLogger.getLogger(logger, "") }
            assertEquals(MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE, error1.message)

            val error2 = assertThrows<IllegalStateException> { ReactiveKLogger.getLogger(logger, " ") }
            assertEquals(MDC_CONTEXT_KEY_IS_EMPTY_MESSAGE, error2.message)
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
    fun scheduler() {
        val customScheduler = Schedulers.newBoundedElastic(10, 10, randomText())
        val loggerWithCustomScheduler = ReactiveKLogger.getLogger(imperativeLogger, scheduler = customScheduler)
        assertSame(loggerWithCustomScheduler.scheduler, customScheduler)
    }
}