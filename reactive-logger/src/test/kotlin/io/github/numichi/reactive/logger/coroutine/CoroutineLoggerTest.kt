package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
            assertEquals(Configuration.defaultReactorContextMdcKey, instance1.contextKey)
        }
    }

    @Test
    fun creationTest() {
        val logger = LoggerFactory.getLogger(this::class.java)

        run {
            val instance1 = CoroutineLogger.getLogger(logger)
            val instance2 = CoroutineLogger.getLogger(logger, "foo")
            val instance3 = CoroutineLogger.getLogger(logger, Schedulers.parallel())
            val instance4 = CoroutineLogger.getLogger(logger, "foo", Schedulers.parallel())
            val instance5 = CoroutineLogger.getLogger(logger, null, null)

            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance4.logger.name)

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
            val instance1 = CoroutineLogger.getLogger("bar")
            val instance2 = CoroutineLogger.getLogger("bar", "foo")
            val instance3 = CoroutineLogger.getLogger("bar", Schedulers.parallel())
            val instance4 = CoroutineLogger.getLogger("bar", "foo", Schedulers.parallel())
            val instance5 = CoroutineLogger.getLogger("bar", null, null)

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
            val instance1 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java)
            val instance2 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java, "foo")
            val instance3 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java, Schedulers.parallel())
            val instance4 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java, "foo", Schedulers.parallel())
            val instance5 = CoroutineLogger.getLogger(CoroutineLoggerTest::class.java, null, null)

            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLoggerTest", instance5.logger.name)

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
    fun getName() {
        val name: String = randomText()
        every { imperativeLogger.name } returns name
        assertEquals(name, logger.logger.name)
    }

    @Test
    fun imperative() {
        assertSame(logger.logger, imperativeLogger)
    }

    @Test
    fun scheduled() {
        assertSame(Schedulers.boundedElastic(), logger.scheduler)
        assertSame(Schedulers.parallel(), loggerScheduled.scheduler)
    }
}
