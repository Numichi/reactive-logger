package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.models.MDC
import io.github.numichi.reactive.logger.models.MDCHook
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context

@ExperimentalCoroutinesApi
class MDCHookContextTest {

    @BeforeEach
    fun setUp() {
        DefaultValues.getInstance().reset()
    }

    @Test
    fun `should lift values from context into MDC by hook format`() {
        runTest {
            val customHook1 = MDCHook<String>(contextKey = "after1", hook = { mapOf("mdcAfter1" to it.uppercase()) }, order = 0)
            val customHook2 = MDCHook<Int>(contextKey = "after2", hook = { mapOf("mdcAfter2" to "${it * 100}") }, order = 0)
            val customHook3 = MDCHook<String>(contextKey = "before1", hook = { mapOf("mdcBefore1" to it.uppercase()) }, order = -1)
            DefaultValues.getInstance().addHook(customHook1)
            DefaultValues.getInstance().addHook(customHook2)
            DefaultValues.getInstance().addHook(customHook3)

            val reactiveContextMap = mapOf(
                "after1" to "aaa",
                "after2" to 11,
                "before1" to "bbb"
            )

            withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                val mdc = readMdc()

                assertEquals(3, mdc.size)
                assertEquals("AAA", mdc["mdcAfter1"])
                assertEquals("1100", mdc["mdcAfter2"])
                assertEquals("BBB", mdc["mdcBefore1"])
            }
        }
    }
}