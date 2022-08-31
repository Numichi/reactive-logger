package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.MDC
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.util.context.Context

@ExperimentalCoroutinesApi
class MDCHookContextTest {

    @BeforeEach
    fun setUp() {
        Configuration.reset()
    }

    @Test
    fun `should lift values from context into MDC by specific hook format`() {
        runTest {
            Configuration.addGenericHook<String>(contextKey = "after1", order = 0) { mapOf("mdcAfter1" to it!!.uppercase()) }
            Configuration.addGenericHook<Int>(contextKey = "after2", order = 0) { mapOf("mdcAfter2" to "${it!! * 100}") }
            Configuration.addGenericHook<String>(contextKey = "before1", order = -1) { mapOf("mdcBefore1" to it!!.uppercase()) }

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

    @Test
    fun `should lift values from context into MDC by hook format`() {
        runTest {
            Configuration.addHook(contextKey = "after1", order = 0) {
                val value = it?.let {
                    if (it is String) {
                        it.uppercase()
                    } else {
                        null
                    }
                } ?: ""

                mapOf("mdcAfter1" to value)
            }

            Configuration.addHook(contextKey = "after2", order = 0) {
                val value = it?.let {
                    if (it is Number) {
                        (it.toInt() * 100).toString()
                    } else {
                        null
                    }
                } ?: 0

                mapOf("mdcAfter2" to value.toString())
            }

            Configuration.addHook(contextKey = "before1", order = -1) {
                val value = it?.let {
                    if (it is String) {
                        it.uppercase()
                    } else {
                        null
                    }
                } ?: ""

                mapOf("mdcBefore1" to value)
            }

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