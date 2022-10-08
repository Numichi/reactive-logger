package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration as RLConfig
import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.scheduler.Schedulers

@SpringBootTest(
    classes = [
        TestConfiguration::class,
        MDCHookAutoConfiguration::class,
        LoggerRegistryImpl::class
    ]
)
class ReactiveLoggerAutoConfigurationTest {

    @Autowired
    lateinit var loggerRegistry: LoggerRegistry

    companion object {
        @BeforeAll
        @JvmStatic
        fun afterEach() {
            Configuration.reset()
        }
    }

    @Test
    fun hookCheck() {
        val hooks = RLConfig.getHooks()
        assertEquals(2, hooks.size)
    }

    @Test
    fun loggerRegistryTest() {
        run {
            val l1 = loggerRegistry.getReactiveLogger("example-instance-1")
            assertEquals("foo", l1.logger.name)
            assertEquals("bar", l1.contextKey)
            assertSame(Schedulers.single(), l1.scheduler)

            val l2 = loggerRegistry.getReactiveLogger("example-instance-2")
            assertEquals("io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl", l2.logger.name)
            assertEquals("bar2", l2.contextKey)
            assertSame(Schedulers.parallel(), l2.scheduler)

            val l3 = loggerRegistry.getReactiveLogger("not-exist-instance")
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveLogger", l3.logger.name)
            assertEquals(Configuration.defaultReactorContextMdcKey, l3.contextKey)
            assertSame(Configuration.defaultScheduler, l3.scheduler)

            val l4 = loggerRegistry.getReactiveLogger("example-instance-2", "test")
            assertEquals("test", l4.logger.name)
            assertEquals("bar2", l4.contextKey)
            assertSame(Schedulers.parallel(), l4.scheduler)

            val l5 = loggerRegistry.getReactiveLogger("example-instance-2", ReactiveLoggerAutoConfigurationTest::class.java)
            assertEquals("io.github.numichi.reactive.logger.spring.ReactiveLoggerAutoConfigurationTest", l5.logger.name)
            assertEquals("bar2", l5.contextKey)
            assertSame(Schedulers.parallel(), l5.scheduler)
        }

        run {
            val l1 = loggerRegistry.getReactiveKLogger("example-instance-1")
            assertEquals("foo", l1.logger.name)
            assertEquals("bar", l1.contextKey)
            assertSame(Schedulers.single(), l1.scheduler)

            val l2 = loggerRegistry.getReactiveKLogger("example-instance-2")
            assertEquals("io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl", l2.logger.name)
            assertEquals("bar2", l2.contextKey)
            assertSame(Schedulers.parallel(), l2.scheduler)

            val l3 = loggerRegistry.getReactiveKLogger("not-exist-instance")
            assertEquals("io.github.numichi.reactive.logger.reactor.ReactiveKLogger", l3.logger.name)
            assertEquals(Configuration.defaultReactorContextMdcKey, l3.contextKey)
            assertSame(Configuration.defaultScheduler, l3.scheduler)

            val l4 = loggerRegistry.getReactiveKLogger("example-instance-2", "test")
            assertEquals("test", l4.logger.name)
            assertEquals("bar2", l4.contextKey)
            assertSame(Schedulers.parallel(), l4.scheduler)

            val l5 = loggerRegistry.getReactiveKLogger("example-instance-2", ReactiveLoggerAutoConfigurationTest::class.java)
            assertEquals("io.github.numichi.reactive.logger.spring.ReactiveLoggerAutoConfigurationTest", l5.logger.name)
            assertEquals("bar2", l5.contextKey)
            assertSame(Schedulers.parallel(), l5.scheduler)
        }

        run {
            val l1 = loggerRegistry.getCoroutineLogger("example-instance-1")
            assertEquals("foo", l1.logger.name)
            assertEquals("bar", l1.contextKey)
            assertSame(Schedulers.single(), l1.scheduler)

            val l2 = loggerRegistry.getCoroutineLogger("example-instance-2")
            assertEquals("io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl", l2.logger.name)
            assertEquals("bar2", l2.contextKey)
            assertSame(Schedulers.parallel(), l2.scheduler)

            val l3 = loggerRegistry.getCoroutineLogger("not-exist-instance")
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineLogger", l3.logger.name)
            assertEquals(Configuration.defaultReactorContextMdcKey, l3.contextKey)
            assertSame(Configuration.defaultScheduler, l3.scheduler)

            val l4 = loggerRegistry.getCoroutineLogger("example-instance-2", "test")
            assertEquals("test", l4.logger.name)
            assertEquals("bar2", l4.contextKey)
            assertSame(Schedulers.parallel(), l4.scheduler)

            val l5 = loggerRegistry.getCoroutineLogger("example-instance-2", ReactiveLoggerAutoConfigurationTest::class.java)
            assertEquals("io.github.numichi.reactive.logger.spring.ReactiveLoggerAutoConfigurationTest", l5.logger.name)
            assertEquals("bar2", l5.contextKey)
            assertSame(Schedulers.parallel(), l5.scheduler)
        }

        run {
            val l1 = loggerRegistry.getCoroutineKLogger("example-instance-1")
            assertEquals("foo", l1.logger.name)
            assertEquals("bar", l1.contextKey)
            assertSame(Schedulers.single(), l1.scheduler)

            val l2 = loggerRegistry.getCoroutineKLogger("example-instance-2")
            assertEquals("io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl", l2.logger.name)
            assertEquals("bar2", l2.contextKey)
            assertSame(Schedulers.parallel(), l2.scheduler)

            val l3 = loggerRegistry.getCoroutineKLogger("not-exist-instance")
            assertEquals("io.github.numichi.reactive.logger.coroutine.CoroutineKLogger", l3.logger.name)
            assertEquals(Configuration.defaultReactorContextMdcKey, l3.contextKey)
            assertSame(Configuration.defaultScheduler, l3.scheduler)

            val l4 = loggerRegistry.getCoroutineKLogger("example-instance-2", "test")
            assertEquals("test", l4.logger.name)
            assertEquals("bar2", l4.contextKey)
            assertSame(Schedulers.parallel(), l4.scheduler)

            val l5 = loggerRegistry.getCoroutineKLogger("example-instance-2", ReactiveLoggerAutoConfigurationTest::class.java)
            assertEquals("io.github.numichi.reactive.logger.spring.ReactiveLoggerAutoConfigurationTest", l5.logger.name)
            assertEquals("bar2", l5.contextKey)
            assertSame(Schedulers.parallel(), l5.scheduler)
        }
    }
}