package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.coroutine.CoroutineKLogger
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.github.numichi.reactive.logger.reactor.ReactiveKLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.scheduler.Schedulers

@SpringBootTest(
    properties = [
        "reactive-logger.contextKey=customKey",
        "reactive-logger.scheduler=immediate",
        "reactive-logger.instances.example.context-key=bar",
    ],
    classes = [
        DefaultValuesAutoConfiguration::class,
        LoggerRegistryImpl::class
    ]
)
class DefaultValuesAutoConfigurationTest {

    @Autowired
    lateinit var loggerRegistry: LoggerRegistry

    @Test
    fun loggerContextKeyTest() {
        val logger1 = ReactiveLogger.getLogger("foo")
        val logger2 = loggerRegistry.getReactiveLogger("example")
        val logger3 = loggerRegistry.getReactiveLogger("example")

        assertEquals("customKey", logger1.contextKey)
        assertEquals("bar", logger2.contextKey)
        assertEquals("bar", logger3.contextKey)

        assertEquals(Schedulers.immediate(), logger1.scheduler)
        assertEquals(Schedulers.immediate(), logger1.scheduler)
        assertEquals(Schedulers.immediate(), logger1.scheduler)

        assertEquals(logger2, logger3)
    }

    @Test
    fun sameObjectTest() {
        val logger11 = loggerRegistry.getReactiveLogger("example")
        val logger12 = loggerRegistry.getReactiveLogger("example")
        val logger21 = loggerRegistry.getReactiveKLogger("example")
        val logger22 = loggerRegistry.getReactiveKLogger("example")
        val logger31 = loggerRegistry.getCoroutineLogger("example")
        val logger32 = loggerRegistry.getCoroutineLogger("example")
        val logger41 = loggerRegistry.getCoroutineKLogger("example")
        val logger42 = loggerRegistry.getCoroutineKLogger("example")

        assertTrue(logger11 == logger12)
        assertTrue(logger21 == logger22)
        assertTrue(logger31 == logger32)
        assertTrue(logger41 == logger42)

        assertTrue(logger11.hashCode() == logger12.hashCode())
        assertTrue(logger21.hashCode() == logger22.hashCode())
        assertTrue(logger31.hashCode() == logger32.hashCode())
        assertTrue(logger41.hashCode() == logger42.hashCode())

        assertTrue(logger11.toString() == logger12.toString())
        assertTrue(logger21.toString() == logger22.toString())
        assertTrue(logger31.toString() == logger32.toString())
        assertTrue(logger41.toString() == logger42.toString())


        val logger1 = ReactiveLogger.getLogger(logger11.logger, "bar")
        val logger2 = ReactiveKLogger.getLogger(logger21.logger, "bar")
        val logger3 = CoroutineLogger.getLogger(logger31.logger, "bar")
        val logger4 = CoroutineKLogger.getLogger(logger41.logger, "bar")

        assertTrue(logger11 == logger1)
        assertTrue(logger21 == logger2)
        assertTrue(logger31 == logger3)
        assertTrue(logger41 == logger4)
    }

    @Test
    fun propertyResetTest() {
        Configuration.defaultReactorContextMdcKey = "foo"
        loggerRegistry.reset()

        assertEquals("customKey", Configuration.defaultReactorContextMdcKey)
    }
}