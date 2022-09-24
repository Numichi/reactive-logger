package io.github.numichi.reactive.logger.configuration

import io.github.numichi.reactive.logger.Configuration as RLConfig
import io.github.numichi.reactive.logger.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.scheduler.Schedulers

@SpringBootTest(
    properties = [
        "reactive-logger.default.key=defaultKey",
        "reactive-logger.default.scheduler=parallel",
    ],
    classes = [
        TestConfiguration::class,
        MDCHookAutoConfiguration::class,
        ReactiveLoggerAutoConfiguration::class
    ]
)
class ReactiveLoggerAutoConfigurationCustomPropertiesTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun afterEach() {
            Configuration.reset()
        }
    }

    @Test
    fun test() {
        assertEquals("defaultKey", RLConfig.defaultReactorContextMdcKey)
        assertEquals(Schedulers.parallel(), RLConfig.defaultScheduler)
    }

    @Test
    fun hookCheck() {
        val hooks = RLConfig.getHooks()
        assertEquals(2, hooks.size)
    }
}