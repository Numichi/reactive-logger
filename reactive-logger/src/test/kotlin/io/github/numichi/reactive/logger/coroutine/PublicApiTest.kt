package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exceptions.ReadException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.util.context.Context

@ExperimentalCoroutinesApi
class PublicApiTest {
    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
    }

    @Test
    fun readMdcTest() =
        runTest {
            run {
                assertThrows<ReadException> { readMdc() }
                assertThrows<ReadException> { readMdc(Context.empty()) }
                assertThrows<ReadException> { readMdc("foo") }
                assertThrows<ReadException> { readMdc(Context.empty(), "foo") }

                val context = Context.of("aaa", "bbb")

                val exception1 = assertThrows<ReadException> { readMdc(context, "aaa") }
                assertEquals("The content type is not java.util.Map<Object, Object>", exception1.message)
                Assertions.assertTrue(exception1.cause is ClassCastException)

                val exception2 = assertThrows<ReadException> { readMdc(context, "asd") }
                assertEquals("asd context key is not contain in context", exception2.message)
                Assertions.assertTrue(exception2.cause is NoSuchElementException)
            }

            run {
                val context =
                    Context.of(
                        mapOf(
                            DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("bar" to "baz"),
                            "foo" to mapOf("bar1" to "baz1"),
                        ),
                    )

                val expected1 = MDC(mapOf("bar" to "baz"))
                val expected2 = MDC("foo", mapOf("bar1" to "baz1"))

                withContext(context.asCoroutineContext()) {
                    val mdc1 = readMdc()
                    val mdc2 = readMdc("foo")
                    assertEquals(expected1, mdc1)
                    assertEquals(expected2, mdc2)
                }

                assertEquals(expected1, readMdc(context))
                assertEquals(expected2, readMdc(context, "foo"))
            }
        }

    @Test
    fun readOrDefaultMdcTest() =
        runTest {
            run {
                assertEquals(MDC(), readOrDefaultMdc())
                assertEquals(MDC(), readOrDefaultMdc(Context.empty()))
                assertEquals(MDC("foo"), readOrDefaultMdc("foo"))
                assertEquals(MDC("foo"), readOrDefaultMdc(Context.empty(), "foo"))
            }

            run {
                val context =
                    Context.of(
                        mapOf(
                            DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("bar" to "baz"),
                            "foo" to mapOf("bar1" to "baz1"),
                        ),
                    )

                val expected1 = MDC(mapOf("bar" to "baz"))
                val expected2 = MDC("foo", mapOf("bar1" to "baz1"))

                withContext(context.asCoroutineContext()) {
                    val mdc1 = readOrDefaultMdc()
                    val mdc2 = readOrDefaultMdc("foo")
                    assertEquals(expected1, mdc1)
                    assertEquals(expected2, mdc2)
                }

                assertEquals(expected1, readOrDefaultMdc(context))
                assertEquals(expected2, readOrDefaultMdc(context, "foo"))
            }
        }

    @Test
    fun snapshotTest() =
        runTest {
            run {
                assertEquals(MDC(), snapshotMdc())
                assertEquals(MDC(), snapshotMdc(Context.empty()))
                assertEquals(MDC("foo"), snapshotMdc("foo"))
                assertEquals(MDC("foo"), snapshotMdc(Context.empty(), "foo"))
            }

//        run {
//            Configuration.addHook("hookName", "aaa") { value, mdc ->
//                check(mdc.contextKey == "foo" && value is String)
//                mapOf("aaa" to value.uppercase())
//            }
//
//            val context = Context.of(
//                mapOf(
//                    DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("bar" to "baz"),
//                    "foo" to mapOf("bar1" to "baz1"),
//                    "aaa" to "bbb"
//                )
//            )
//
//            val expected1 = MDC(mapOf("bar" to "baz"))
//            val expected2 = MDC("foo", mapOf("bar1" to "baz1", "aaa" to "BBB"))
//
//            withContext(context.asCoroutineContext()) {
//                val mdc1 = snapshotMdc()
//                val mdc2 = snapshotMdc("foo")
//                assertEquals(expected1, mdc1)
//                assertEquals(expected2, mdc2)
//            }
//
//            assertEquals(expected1, snapshotMdc(context))
//            assertEquals(expected2, snapshotMdc(context, "foo"))
//        }
        }

    @Test
    fun withMDCContextTest() =
        runTest {
            val mdc1 = MDC(mapOf("bar" to "baz"))
            val mdc2 = MDC("foo", mapOf("bar1" to "baz1"))
            val mdc3 = MDC(mapOf("bar2" to "baz2"))

            val context =
                Context.of(
                    mapOf(
                        DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("aaa" to "bbb"),
                        "foo" to mapOf("ccc" to "ddd"),
                    ),
                )

            withMDCContext(mdc1, mdc2) {
                assertEquals(mdc1, readMdc())
                assertEquals(mdc2, readMdc("foo"))
            }

            withMDCContext(context, mdc1, mdc2) {
                assertEquals(mdc1, readMdc())
                assertEquals(mdc2, readMdc("foo"))
            }

            withMDCContext(context) {
                assertEquals(MDC("aaa" to "bbb"), readMdc())
                assertEquals(MDC("foo", "ccc" to "ddd"), readMdc("foo"))
            }

            assertThrows<IllegalStateException> {
                withMDCContext(mdc1, mdc3) {}
            }

            assertThrows<IllegalStateException> {
                withMDCContext(context, mdc1, mdc3) {}
            }
        }
}
