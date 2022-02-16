package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.MDC
import io.github.numichi.reactive.logger.exception.ContextNotExistException
import io.github.numichi.reactive.logger.exception.InvalidContextDataException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.util.context.Context

@ExperimentalCoroutinesApi
internal class MDCContextTest {

    companion object {
        const val ANOTHER_CONTEXT_KEY = "another-context-key"
    }

    @Test
    fun `should give the Context with new context maps`() = runTest {
        val mdc1 = MDC()
        mdc1["mdcKey1"] = "mdcValue1"
        val mdc2 = MDC(ANOTHER_CONTEXT_KEY)
        mdc2["mdcKey2"] = "mdcValue2"

        val result0 = putMdc(Context.empty())
        assertEquals(0, result0.size())

        val result1 = putMdc(Context.empty(), mdc1)
        assertEquals(mdc1, result1.getOrEmpty<Map<*, *>>(DefaultValues.getInstance().defaultReactorContextMdcKey).orElse(null))
        assertEquals(1, result1.size())

        val result2 = putMdc(Context.empty(), mdc1, mdc2)
        assertEquals(mdc1, result2.getOrEmpty<Map<*, *>>(DefaultValues.getInstance().defaultReactorContextMdcKey).orElse(null))
        assertEquals(mdc2, result2.getOrEmpty<Map<*, *>>(ANOTHER_CONTEXT_KEY).orElse(null))
        assertEquals(2, result2.size())
    }

    @Test
    fun `should give the MDC you are looking for (default key)`() = runTest {
        val mdc = MDC()
        mdc["mdcKey"] = "mdcValue"

        withMDCContext(mdc) {
            val result = readMdc()
            assertEquals(mdc, result)
        }

        withMDCContext(mdc) {
            val result = readMdc(coroutineContext[ReactorContext]?.context)
            assertEquals(mdc, result)
        }

        withMDCContext(Context.of(mutableMapOf("1" to "2")), mdc) {
            val size = coroutineContext[ReactorContext]?.context?.size()
            val result = readMdc(coroutineContext[ReactorContext]?.context)
            assertEquals(mdc, result)
            assertEquals(2, size)
        }
    }

    @Test
    fun `should give the MDC you are looking for (default key + use native withContext)`() = runTest {
        val mdcMap = mutableMapOf<String, String>()
        mdcMap["mdcKey"] = "mdcValue"

        withContext(Context.of(mapOf(DefaultValues.getInstance().defaultReactorContextMdcKey to mdcMap)).asCoroutineContext()) {
            val result = readMdc(coroutineContext[ReactorContext]?.context)
            assertEquals(mdcMap, result)
            assertEquals(mdcMap, result)
        }
    }

    @Test
    fun `should give the MDC you are looking for (another key)`() = runTest {
        val mdc2 = MDC(ANOTHER_CONTEXT_KEY)
        mdc2["mdcKey"] = "mdcValue"

        withMDCContext(mdc2) {
            val result = readMdc(ANOTHER_CONTEXT_KEY)
            assertEquals(mdc2, result)
        }
    }

    @Test
    fun `should give the MDC you are looking for (more MDC Context)`() = runTest {
        val mdc1 = MDC()
        mdc1["mdcKey"] = "mdcValue"

        val mdc2 = MDC(ANOTHER_CONTEXT_KEY)
        mdc2["mdcKey"] = "mdcValue"

        withMDCContext(mdc1, mdc2) {
            val result1 = readMdc(coroutineContext[ReactorContext]?.context)
            val result2 = readMdc(coroutineContext[ReactorContext]?.context, DefaultValues.getInstance().defaultReactorContextMdcKey)
            val result3 = readMdc(DefaultValues.getInstance().defaultReactorContextMdcKey)
            val result4 = readMdc(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY)
            val result5 = readMdc(ANOTHER_CONTEXT_KEY)

            assertEquals(mdc1, result1)
            assertEquals(mdc1, result2)
            assertEquals(mdc1, result3)
            assertEquals(mdc2, result4)
            assertEquals(mdc2, result5)
        }
    }

    @Test
    fun `should need to be able to store a map`() = runTest {
        val mdcMap = mutableMapOf<String, String>()
        mdcMap["mdcKey"] = "mdcValue"

        withMDCContext(MDC(mdcMap)) {
            val result1 = readMdc(coroutineContext[ReactorContext]?.context)
            val result2 = readMdc()
            val result3 = readMdcOrNull(coroutineContext[ReactorContext]?.context)
            val result4 = readMdcOrNull()
            assertEquals(mdcMap, result1)
            assertEquals(mdcMap, result2)
            assertEquals(mdcMap, result3)
            assertEquals(mdcMap, result4)
        }

        withMDCContext(MDC(ANOTHER_CONTEXT_KEY, mdcMap)) {
            val result = readMdc(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY)
            assertEquals(mdcMap, result)
        }
    }

    @Test
    fun `should be MDC stored with the overridden context ID`() = runTest {
        val mdcMap = mutableMapOf<String, String>()
        mdcMap["mdcKey"] = "mdcValue"

        withMDCContext(MDC(mdcMap)) {
            val result1 = readMdc(coroutineContext[ReactorContext]?.context)
            val result2 = readMdcOrNull(coroutineContext[ReactorContext]?.context)
            assertEquals(mdcMap, result1)
            assertEquals(mdcMap, result2)
        }

        withMDCContext(MDC(ANOTHER_CONTEXT_KEY, mdcMap)) {
            val result1 = readMdc(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY)
            val result2 = readMdcOrNull(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY)
            assertEquals(mdcMap, result1)
            assertEquals(mdcMap, result2)
        }
    }

    @Test
    fun `should get null if context data is invalid`() = runTest {
        withContext(Context.empty().asCoroutineContext()) {
            assertNull(readMdcOrNull())
        }

        assertNull(readMdcOrNull(null))
        assertNull(readMdcOrNull(null, ""))
    }

    @Test
    fun `should throw ContextNotExistException if context data is invalid`() = runTest {
        withContext(Context.empty().asCoroutineContext()) {
            assertThrows<ContextNotExistException> { readMdc() }
        }
    }

    @Test
    fun `should throw InvalidContextDataException if context data is invalid`() = runTest {
        withContext(Context.of(mapOf(DefaultValues.getInstance().defaultReactorContextMdcKey to 10)).asCoroutineContext()) {
            assertThrows<InvalidContextDataException> { readMdc() }
            assertNull(readMdcOrNull())
        }

        withContext(Context.of(mapOf(DefaultValues.getInstance().defaultReactorContextMdcKey to "any")).asCoroutineContext()) {
            assertThrows<InvalidContextDataException> { readMdc() }
            assertNull(readMdcOrNull())
        }
    }

    @Test
    fun `should throw IllegalArgumentException if any parameter is NULL`() = runTest {
        assertThrows<IllegalArgumentException> { withMDCContext(null, MDC()) {} }
        assertThrows<IllegalArgumentException> { readMdc(null) }
        assertThrows<IllegalArgumentException> { readMdc(null, "any-key") }
    }
}