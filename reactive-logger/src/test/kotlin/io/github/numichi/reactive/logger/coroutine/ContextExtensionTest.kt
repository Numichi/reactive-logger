package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exceptions.ReadException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.util.context.Context

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
            val ctx1 = context.modifyMdc(mapOf("aaa" to "bbb"))
            val ctx2 = context.modifyMdc("foo", mapOf("aaa" to "bbb"))

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
            val mdc1 = MDC("aaa" to "bbb")
            val mdc2 = MDC("foo", "aaa" to "bbb")
            val ctx1 = context.merge(mdc1)
            val ctx2 = context.merge(mdc2)

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
        assertEquals("The content type is not java.util.Map<String, String?>", exception1.message)
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
        assertEquals("The content type is not java.util.Map<String, String?>", exception1.message)
        assertTrue(exception1.cause is ClassCastException)

        val exception2 = assertThrows<ReadException> { context.getMdc("asd") }
        assertEquals("asd context key is not contain in context", exception2.message)
        assertTrue(exception2.cause is NoSuchElementException)
    }

    @Test
    fun snapshotMdcTest() {
        Configuration.addHook("hookName", "aaa") { value, mdc ->
            check(mdc.contextKey == "foo" && value is String)
            mapOf("aaa" to value.uppercase())
        }

        assertEquals(MDC("bar" to "baz"), context.snapshotMdc())
        assertEquals(MDC("bar" to "baz"), context.readOnly().snapshotMdc())

        assertEquals(MDC("xxx"), context.snapshotMdc("xxx"))
        assertEquals(MDC("xxx"), context.readOnly().snapshotMdc("xxx"))

        assertEquals(MDC("foo", mapOf("fooBar" to "fooBaz", "aaa" to "BBB")), context.snapshotMdc("foo"))
        assertEquals(MDC("foo", mapOf("fooBar" to "fooBaz", "aaa" to "BBB")), context.readOnly().snapshotMdc("foo"))
    }
}