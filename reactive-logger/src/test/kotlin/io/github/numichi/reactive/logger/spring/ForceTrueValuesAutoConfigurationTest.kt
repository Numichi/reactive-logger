package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.scheduler.Schedulers

@SpringBootTest(
    properties = [
        "reactive-logger.forceUse=true",
        "reactive-logger.contextKey=customKey",
        "reactive-logger.scheduler=parallel",
    ],
    classes = [
        DefaultValuesAutoConfiguration::class,
    ],
)
class ForceTrueValuesAutoConfigurationTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Configuration.reset()
            Configuration.defaultReactorContextMdcKey = "foo"
            Configuration.defaultScheduler = Schedulers.immediate()
        }
    }

    @Test
    fun configurationTest() {
        assertEquals("customKey", Configuration.defaultReactorContextMdcKey)
        assertEquals(Schedulers.parallel(), Configuration.defaultScheduler)
    }
}
