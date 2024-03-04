package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.randomText
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
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
import reactor.core.scheduler.Schedulers

@ExperimentalCoroutinesApi
internal class CoroutineKLoggerTest {
    private val imperativeLogger: KLogger = mockk(relaxed = true)
    private val logger = CoroutineKLogger.getLogger(imperativeLogger)

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
        clearMocks(imperativeLogger)
    }

    @Test
    fun createReactiveLogger() {
        runTest {
            val instance1 = CoroutineKLogger.getLogger(imperativeLogger)
            assertNotNull(instance1)
            assertEquals(Configuration.defaultReactorContextMdcKey, instance1.contextKey)
        }
    }

    @Test
    fun creationTest() {
        val logger = KotlinLogging.logger(this::class.java.name)

        run {
            val instance1 = CoroutineKLogger.getLogger(logger)
            val instance2 = CoroutineKLogger.getLogger(logger, "foo")
            val instance3 = CoroutineKLogger.getLogger(logger, Schedulers.parallel())
            val instance4 = CoroutineKLogger.getLogger(logger, "foo", Schedulers.parallel())
            val instance5 = CoroutineKLogger.getLogger(logger, null, null)

            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance4.logger.name)

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
            val instance1 = CoroutineKLogger.getLogger(logger)
            val instance2 = CoroutineKLogger.getLogger(logger, "foo")
            val instance3 = CoroutineKLogger.getLogger(logger, Schedulers.parallel())
            val instance4 = CoroutineKLogger.getLogger(logger, "foo", Schedulers.parallel())
            val instance5 = CoroutineKLogger.getLogger(logger, null, null)

            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance5.logger.name)

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
            val instance1 = CoroutineKLogger.getLogger {}
            val instance2 = CoroutineKLogger.getLogger({}, "foo")
            val instance3 = CoroutineKLogger.getLogger({}, Schedulers.parallel())
            val instance4 = CoroutineKLogger.getLogger({}, "foo", Schedulers.parallel())
            val instance5 = CoroutineKLogger.getLogger({}, null, null)

            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance5.logger.name)

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
            val instance1 = CoroutineKLogger.getLogger("bar")
            val instance2 = CoroutineKLogger.getLogger("bar", "foo")
            val instance3 = CoroutineKLogger.getLogger("bar", Schedulers.parallel())
            val instance4 = CoroutineKLogger.getLogger("bar", "foo", Schedulers.parallel())
            val instance5 = CoroutineKLogger.getLogger("bar", null, null)

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
            val instance1 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java)
            val instance2 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java, "foo")
            val instance3 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java, Schedulers.parallel())
            val instance4 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java, "foo", Schedulers.parallel())
            val instance5 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java, null, null)

            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance1.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance2.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance3.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance4.logger.name)
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", instance5.logger.name)

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
    fun getNameTest() {
        val name: String = randomText()
        every { imperativeLogger.name } returns name
        assertEquals(name, logger.logger.name)
    }

    @Test
    fun sameLoggerTest() {
        assertSame(logger.reactiveLogger.logger, imperativeLogger)
    }

    @Test
    fun contextKey() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = CoroutineKLogger.getLogger(imperativeLogger, contextKey = contextKey)
        assertSame(loggerWithCustomScheduler.contextKey, contextKey)
    }
}
