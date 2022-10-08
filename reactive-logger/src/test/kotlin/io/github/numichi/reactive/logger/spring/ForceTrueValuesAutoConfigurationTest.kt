package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "reactive-logger.forceUse=true",
        "reactive-logger.contextKey=customKey",
    ],
    classes = [
        DefaultValuesAutoConfiguration::class,
        MDCHookAutoConfiguration::class,
        LoggerRegistryImpl::class
    ]
)
class ForceTrueValuesAutoConfigurationTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            Configuration.reset()
            Configuration.defaultReactorContextMdcKey = "foo"
        }
    }

    @Test
    fun configurationTest() {
        assertEquals("customKey", Configuration.defaultReactorContextMdcKey)
    }
}