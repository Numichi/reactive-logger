package io.github.numichi.reactive.logger.hook

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.coroutine.readMdc
import io.github.numichi.reactive.logger.reactor.MDCContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context

@ExperimentalCoroutinesApi
internal class MDCHookProcessTest {

    @BeforeEach
    fun setUp() {
        Configuration.reset()
    }

    @Nested
    inner class Coroutine {

        @Test
        fun `should lift values from context into MDC by hook format`() {
            Configuration.addGenericHook<String>(name = "key1", contextKey = "after1") { it, _ -> mapOf("mdcAfter1" to it!!.uppercase()) }
            Configuration.addGenericHook<Int>(name = "key2", contextKey = "after2") { it, _ -> mapOf("mdcAfter2" to "${it!! * 100}") }
            Configuration.addGenericHook<String>(name = "key3", contextKey = "before1") { it, _ -> mapOf("mdcBefore1" to it!!.uppercase()) }

            val reactiveContextMap = mapOf(
                "after1" to "aaa",
                "after2" to 11,
                "before1" to "bbb",
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("currentMdcKey" to "currentMdcValue")
            )

            runTest {
                withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                    val mdc = readMdc()

                    assertEquals(4, mdc.size)
                    assertEquals("AAA", mdc["mdcAfter1"])
                    assertEquals("1100", mdc["mdcAfter2"])
                    assertEquals("BBB", mdc["mdcBefore1"])
                    assertEquals("currentMdcValue", mdc["currentMdcKey"])
                }
            }
        }

        @Test
        fun `should be overwritten before hook with after hook`() {
            Configuration.addGenericHook<Int>(name = "key1", contextKey = "key", order = 0) { it, _ -> mapOf("hookKey" to "${it!! * 2}") }
            Configuration.addGenericHook<Int>(name = "key2", contextKey = "key", order = -1) { it, _ -> mapOf("hookKey" to "${it!! * 3}") }

            val reactiveContextMap = mapOf("key" to 1)

            runTest {
                withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                    val mdc = readMdc()

                    assertEquals(1, mdc.size)
                    assertEquals("2", mdc["hookKey"])
                }
            }
        }

        @Test
        fun `should not effect when lambda throw any exception`() {
            Configuration.addHook(name = "key1", contextKey = "after1") { _, _ ->
                throw Exception()
            }

            val reactiveContextMap = mapOf(
                "after1" to "aaa",

                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("currentMdcKey" to "currentMdcValue")
            )

            runTest {
                withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                    val mdc = readMdc()

                    assertEquals(1, mdc.size)
                    assertEquals("currentMdcValue", mdc["currentMdcKey"])
                }
            }
        }

        @Test
        fun `should run hook function with null when access to not exist contextKey`() {
            Configuration.addHook(name = "key1", contextKey = "after1") { it, _ ->
                mapOf("hookKey" to "$it")
            }

            val reactiveContextMap = mapOf(
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("currentMdcKey" to "currentMdcValue")
            )

            runTest {
                withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                    val mdc = readMdc()

                    assertEquals(2, mdc.size)
                    assertEquals("currentMdcValue", mdc["currentMdcKey"])
                    assertEquals("null", mdc["hookKey"])
                }
            }
        }

        @Test
        fun `should handle specific class in hook`() {
            Configuration.addGenericHook<TestHelperClass>(name = "key1", contextKey = TestHelperClass::class.java) { clazz, _ ->
                requireNotNull(clazz)
                mapOf("int" to clazz.getInt().toString())
            }

            val reactiveContextMap = mapOf(
                TestHelperClass::class.java to TestHelperClass(),
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("currentMdcKey" to "currentMdcValue")
            )

            runTest {
                withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                    val mdc = readMdc()

                    assertEquals(2, mdc.size)
                    assertEquals("currentMdcValue", mdc["currentMdcKey"])
                    assertEquals("222", mdc["int"])
                }
            }
        }

        @Test
        fun `should handle specific class in hook just throws ClassCastException in background`() {
            Configuration.addGenericHook<TestHelperClass>(name = "key1", contextKey = TestHelperClass::class.java) { clazz, _ ->
                mapOf("int" to clazz?.getInt().toString())
            }

            val reactiveContextMap = mapOf(
                TestHelperClass::class.java to TestAnotherHelperClass(),
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("currentMdcKey" to "currentMdcValue")
            )

            runTest {
                withContext(Context.of(reactiveContextMap).asCoroutineContext()) {
                    val mdc = readMdc()

                    assertEquals(2, mdc.size)
                    assertEquals("currentMdcValue", mdc["currentMdcKey"])
                    assertEquals("null", mdc["int"])
                }
            }
        }
    }

    @Nested
    inner class Reactor {
        @Test
        fun `should lift values from context into MDC by hook format`() {
            Configuration.addGenericHook<String>(name = "key1", contextKey = "after1") { it, _ -> mapOf("mdcAfter1" to it!!.uppercase()) }
            Configuration.addGenericHook<Int>(name = "key2", contextKey = "after2") { it, _ -> mapOf("mdcAfter2" to "${it!! * 100}") }
            Configuration.addGenericHook<String>(name = "key3", contextKey = "before1") { it, _ -> mapOf("mdcBefore1" to it!!.uppercase()) }

            val reactiveContextMap = mapOf(
                "after1" to "aaa",
                "after2" to 11,
                "before1" to "bbb",
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("currentMdcKey" to "currentMdcValue")
            )

            val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
                .contextWrite { it.putAllMap(reactiveContextMap) }

            StepVerifier.create(resultDefault)
                .expectNextMatches {
                    it.size == 4
                        && it["mdcAfter1"] == "AAA"
                        && it["mdcAfter2"] == "1100"
                        && it["mdcBefore1"] == "BBB"
                        && it["currentMdcKey"] == "currentMdcValue"
                }
                .verifyComplete()
        }

        @Test
        fun `should be overwritten before hook with after hook`() {
            Configuration.addGenericHook<Int>(name = "key1", contextKey = "key", order = 0) { it, _ -> mapOf("hookKey" to "${it!! * 2}") }
            Configuration.addGenericHook<Int>(name = "key2", contextKey = "key", order = -1) { it, _ -> mapOf("hookKey" to "${it!! * 3}") }

            val reactiveContextMap = mapOf("key" to 1)

            val resultDefault: Mono<MDC> = Mono.defer { MDCContext.read() }
                .contextWrite { it.putAllMap(reactiveContextMap) }

            StepVerifier.create(resultDefault)
                .expectNextMatches { it.size == 1 && it["hookKey"] == "2" }
                .verifyComplete()
        }
    }
}