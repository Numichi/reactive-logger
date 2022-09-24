package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.randomText
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import mu.KLogger
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
            assertEquals(Configuration.defaultReactorContextMdcKey, instance1.mdcContextKey)
        }
    }

    @Test
    fun creationTest() {
        val logger = LoggerFactory.getLogger(this::class.java)
        val klogger = KotlinLogging.logger(logger)

        val i1 = CoroutineKLogger.getLogger(logger)
        val i2 = CoroutineKLogger.getLogger(logger, "foo", Schedulers.single())
        val i3 = CoroutineKLogger.getLogger(klogger)
        val i4 = CoroutineKLogger.getLogger(klogger, "foo", Schedulers.single())
        val i5 = CoroutineKLogger.getLogger({})
        val i6 = CoroutineKLogger.getLogger({}, "foo", Schedulers.single())
        val i7 = CoroutineKLogger.getLogger("foobar")
        val i8 = CoroutineKLogger.getLogger("foobar", "foo", Schedulers.single())
        val i9 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java)
        val i10 = CoroutineKLogger.getLogger(CoroutineKLoggerTest::class.java, "foo", Schedulers.single())


        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i1.logger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i1.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i1.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i2.logger.underlyingLogger.name)
        assertEquals("foo", i2.mdcContextKey)
        assertSame(Schedulers.single(), i2.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i3.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i3.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i3.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i4.logger.underlyingLogger.name)
        assertEquals("foo", i4.mdcContextKey)
        assertSame(Schedulers.single(), i4.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i5.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i5.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i5.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i6.logger.underlyingLogger.name)
        assertEquals("foo", i6.mdcContextKey)
        assertSame(Schedulers.single(), i6.scheduler)

        assertEquals("foobar", i7.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i7.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i7.scheduler)

        assertEquals("foobar", i8.logger.underlyingLogger.name)
        assertEquals("foo", i8.mdcContextKey)
        assertSame(Schedulers.single(), i8.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i9.logger.underlyingLogger.name)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, i9.mdcContextKey)
        assertSame(Schedulers.boundedElastic(), i9.scheduler)

        assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLoggerTest", i10.logger.underlyingLogger.name)
        assertEquals("foo", i10.mdcContextKey)
        assertSame(Schedulers.single(), i10.scheduler)
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
        assertSame(logger.reactiveLogger.logger.underlyingLogger, imperativeLogger.underlyingLogger)
    }

    // TODO: Delete on v3.3.0
    @Test
    fun slf4jLogger() {
        assertSame(logger.reactorLogger.slf4jLogger, imperativeLogger.underlyingLogger)
    }

    @Test
    fun contextKey() {
        val contextKey = "another-context-key"
        val loggerWithCustomScheduler = CoroutineKLogger.getLogger(imperativeLogger, mdcContextKey = contextKey)
        assertSame(loggerWithCustomScheduler.mdcContextKey, contextKey)
    }

    @Test
    fun wrapResultTest() {
        runTest {
            val log = mockk<Logger>(relaxed = true)
            val logger = CoroutineKLogger.getLogger(log)
            logger.wrapResult { it.info("") }
            verify(exactly = 1) { log.info("") }
        }

        runTest {
            withMDCContext(MDC()) {
                val log = mockk<Logger>(relaxed = true)
                val logger = CoroutineKLogger.getLogger(log)
                logger.wrapResult { it.info("") }
                verify(exactly = 1)  { log.info("") }
            }
        }
    }
}
