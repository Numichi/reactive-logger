package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exceptions.ReadException
import io.github.numichi.reactive.logger.hook.Position
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.util.context.Context
import reactor.util.function.Tuples

@ExperimentalCoroutinesApi
class ContextExtensionTest {

    private lateinit var context: Context

    @BeforeEach
    fun beforeEach() {
        Configuration.reset()
        context = Context.of(
            mapOf(
                DEFAULT_REACTOR_CONTEXT_MDC_KEY to mapOf("bar" to "baz"),
                "foo" to mapOf("fooBar" to "fooBaz"),
                "aaa" to "bbb"
            )
        )
    }

    @Test
    fun modifyMdcTest() {
        run {
            val ctx1 = context.modifyMdc { it + ("aaa" to "bbb") }
            val ctx2 = context.modifyMdc("foo") { it + ("aaa" to "bbb") }

            val expected1 = mapOf("bar" to "baz")
            val expected2 = mapOf("bar" to "baz", "aaa" to "bbb")
            val expected3 = mapOf("fooBar" to "fooBaz")
            val expected4 = mapOf("fooBar" to "fooBaz", "aaa" to "bbb")

            assertEquals(expected2, ctx1.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected1, ctx2.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected3, ctx1.get<Map<String, String?>>("foo"))
            assertEquals(expected4, ctx2.get<Map<String, String?>>("foo"))
        }

        run {
            val ctx01 = context.modifyMdc(mapOf("aaa" to "bbb"))
            val ctx02 = context.modifyMdc(Tuples.of("aaa", "bbb"))
            val ctx03 = context.modifyMdc("aaa" to "bbb")
            val ctx04 = context.modifyMdc(arrayOf(Tuples.of("aaa", "bbb")))
            val ctx05 = context.modifyMdc(arrayOf("aaa" to "bbb"))

            val ctx11 = context.modifyMdc("foo", mapOf("aaa" to "bbb"))
            val ctx12 = context.modifyMdc("foo", Tuples.of("aaa", "bbb"))
            val ctx13 = context.modifyMdc("foo", "aaa" to "bbb")
            val ctx14 = context.modifyMdc("foo", arrayOf(Tuples.of("aaa", "bbb")))
            val ctx15 = context.modifyMdc("foo", arrayOf("aaa" to "bbb"))

            val expected1 = mapOf("bar" to "baz", "aaa" to "bbb")
            val expected2 = mapOf("fooBar" to "fooBaz", "aaa" to "bbb")
            val expected3 = mapOf("bar" to "baz")
            val expected4 = mapOf("fooBar" to "fooBaz")

            assertEquals(expected1, ctx01.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected1, ctx02.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected1, ctx03.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected1, ctx04.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected1, ctx05.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected2, ctx11.get<Map<String, String?>>("foo"))
            assertEquals(expected2, ctx12.get<Map<String, String?>>("foo"))
            assertEquals(expected2, ctx13.get<Map<String, String?>>("foo"))
            assertEquals(expected2, ctx14.get<Map<String, String?>>("foo"))
            assertEquals(expected2, ctx15.get<Map<String, String?>>("foo"))

            assertEquals(expected3, ctx11.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected3, ctx12.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected3, ctx13.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected3, ctx14.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected3, ctx15.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected4, ctx01.get<Map<String, String?>>("foo"))
            assertEquals(expected4, ctx02.get<Map<String, String?>>("foo"))
            assertEquals(expected4, ctx03.get<Map<String, String?>>("foo"))
            assertEquals(expected4, ctx04.get<Map<String, String?>>("foo"))
            assertEquals(expected4, ctx05.get<Map<String, String?>>("foo"))
        }

        run {
            val mdc1 = MDC("aaa" to "bbb")
            val mdc2 = MDC("foo", "aaa" to "bbb")
            val ctx1 = context.modifyMdc(mdc1)
            val ctx2 = context.modifyMdc(mdc2)

            val expected1 = mapOf("bar" to "baz")
            val expected2 = mapOf("bar" to "baz", "aaa" to "bbb")
            val expected3 = mapOf("fooBar" to "fooBaz")
            val expected4 = mapOf("fooBar" to "fooBaz", "aaa" to "bbb")

            assertEquals(expected2, ctx1.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected1, ctx2.get<Map<String, String?>>(DEFAULT_REACTOR_CONTEXT_MDC_KEY))
            assertEquals(expected3, ctx1.get<Map<String, String?>>("foo"))
            assertEquals(expected4, ctx2.get<Map<String, String?>>("foo"))
        }
    }

    @Test
    fun getMdcTest() {
        val expected1 = MDC("bar" to "baz")
        val expected2 = MDC("foo", "fooBar" to "fooBaz")

        assertEquals(expected1, context.getMdc())
        assertEquals(expected2, context.getMdc("foo"))

        val exception1 = assertThrows<ReadException> { context.getMdc("aaa") }
        assertEquals("The content type is not java.util.Map<Object, Object>", exception1.message)
        assertTrue(exception1.cause is ClassCastException)

        val exception2 = assertThrows<ReadException> { context.getMdc("asd") }
        assertEquals("asd context key is not contain in context", exception2.message)
        assertTrue(exception2.cause is NoSuchElementException)
    }

    @Test
    fun getOrDefaultMdcTest() {
        val expected1 = MDC("bar" to "baz")
        val expected2 = MDC("foo", "fooBar" to "fooBaz")

        assertEquals(expected1, context.getOrDefaultMdc())
        assertEquals(expected2, context.getOrDefaultMdc("foo"))

        val exception1 = assertThrows<ReadException> { context.getMdc("aaa") }
        assertEquals("The content type is not java.util.Map<Object, Object>", exception1.message)
        assertTrue(exception1.cause is ClassCastException)

        val exception2 = assertThrows<ReadException> { context.getMdc("asd") }
        assertEquals("asd context key is not contain in context", exception2.message)
        assertTrue(exception2.cause is NoSuchElementException)
    }

    @Test
    fun snapshotMdcTest() {
        Configuration.addHook(Position.BEFORE) { _, mdc ->
            check(mdc.contextKey == "foo")

            mapOf("aaa" to "BBB")
        }

        assertEquals(MDC("bar" to "baz"), context.snapshotMdc())
        assertEquals(MDC("bar" to "baz"), context.readOnly().snapshotMdc())

        assertEquals(MDC("xxx"), context.snapshotMdc("xxx"))
        assertEquals(MDC("xxx"), context.readOnly().snapshotMdc("xxx"))

        assertEquals(MDC("foo", mapOf("fooBar" to "fooBaz", "aaa" to "BBB")), context.snapshotMdc("foo"))
        assertEquals(MDC("foo", mapOf("fooBar" to "fooBaz", "aaa" to "BBB")), context.readOnly().snapshotMdc("foo"))
    }
}