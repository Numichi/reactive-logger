package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.spring.beans.LoggerRegistryImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "reactive-logger.forceUse=false",
        "reactive-logger.contextKey=customKey",
    ],
    classes = [
        DefaultValuesAutoConfiguration::class,
        LoggerRegistryImpl::class
    ]
)
class ForceFalseValuesAutoConfigurationTest {

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
           assertEquals("foo", Configuration.defaultReactorContextMdcKey)
    }
}