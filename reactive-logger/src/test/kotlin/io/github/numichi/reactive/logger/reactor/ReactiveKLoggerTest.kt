package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.scheduler.Schedulers

@ExperimentalCoroutinesApi
internal class ReactiveKLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = ReactiveKLogger.getLogger(imperativeLogger)
    private val log = ReactiveKLogger.getLogger {}

    @BeforeEach
    fun afterEach() {
        clearAllMocks()
        Configuration.reset()
    }

    @Test
    fun creationTest() {
        val logger = KotlinLogging.logger(this::class.java.name)

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
            val instance2 = ReactiveKLogger.getLogger({}, "foo")
            val instance3 = ReactiveKLogger.getLogger({}, Schedulers.parallel())
            val instance4 = ReactiveKLogger.getLogger({}, "foo", Schedulers.parallel())
            val instance5 = ReactiveKLogger.getLogger({}, null, null)

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