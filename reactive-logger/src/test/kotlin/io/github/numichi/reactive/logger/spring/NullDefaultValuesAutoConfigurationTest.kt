package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistry
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.scheduler.Schedulers

@SpringBootTest(
    classes = [
        DefaultValuesAutoConfiguration::class,
        LoggerRegistryImpl::class
    ]
)
class NullDefaultValuesAutoConfigurationTest {
    companion object {

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Configuration.reset()
        }
    }

    @Autowired
    lateinit var loggerRegistry: LoggerRegistry

    // non load application.yml
    @Test
    fun loggerContextKeyTest() {
        val logger1 = ReactiveLogger.getLogger("foo")
        val logger2 = loggerRegistry.getReactiveLogger("example")
        val logger3 = loggerRegistry.getReactiveLogger("example")

        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, logger1.contextKey)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, logger2.contextKey)
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, logger3.contextKey)

        assertEquals(Schedulers.boundedElastic(), logger1.scheduler)
        assertEquals(Schedulers.boundedElastic(), logger1.scheduler)
        assertEquals(Schedulers.boundedElastic(), logger1.scheduler)

        assertEquals(logger2, logger3)
    }
}