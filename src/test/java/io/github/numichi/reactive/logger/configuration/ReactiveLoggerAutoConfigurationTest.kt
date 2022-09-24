package io.github.numichi.reactive.logger.configuration

import io.github.numichi.reactive.logger.Configuration as RLConfig
import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.scheduler.Schedulers

@SpringBootTest(
    classes = [
        TestConfiguration::class,
        MDCHookAutoConfiguration::class,
        ReactiveLoggerAutoConfiguration::class
    ]
)
class ReactiveLoggerAutoConfigurationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun afterEach() {
            Configuration.reset()
        }
    }

    @Test
    fun test() {
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY, RLConfig.defaultReactorContextMdcKey)
        assertEquals(Schedulers.boundedElastic(), RLConfig.defaultScheduler)
    }

    @Test
    fun hookCheck() {
        val hooks = RLConfig.getHooks()
        assertEquals(2, hooks.size)
    }
}