package io.github.numichi.reactive.logger.spring

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.coroutine.CoroutineLogger
import io.mockk.clearMocks
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.util.context.Context

@SpringBootTest(
    classes = [
        TestConfig::class,
        MDCContextHookAutoConfiguration::class,
    ],
)
class ReactiveLoggerAutoConfigurationTest {
    @Autowired
    lateinit var hookMock: HookMock

    companion object {
        @BeforeAll
        @JvmStatic
        fun afterEach() {
            Configuration.reset()
        }
    }

    @BeforeEach
    fun beforeEach() {
        clearMocks(hookMock.mock)
    }

    @Test
    fun hookCheck() {
        val hooks = Configuration.getContextHooks()
        assertEquals(2, hooks.size)
    }

    @Test
    @ExperimentalCoroutinesApi
    fun contextHookTest() {
        runTest {
            val logger = CoroutineLogger.getLogger(ReactiveLoggerAutoConfigurationTest::class.java)

            withContext(Context.of("before", "aaa").asCoroutineContext()) {
                logger.info("")
            }

            verify { hookMock.mock("aaa") }
        }
    }
}
