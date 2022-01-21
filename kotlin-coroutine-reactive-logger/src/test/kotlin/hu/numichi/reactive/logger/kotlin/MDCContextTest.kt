package hu.numichi.reactive.logger.kotlin

import hu.numichi.reactive.logger.Consts
import hu.numichi.reactive.logger.Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY
import hu.numichi.reactive.logger.MDC
import hu.numichi.reactive.logger.exception.InvalidContextDataException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.util.context.Context

@ExperimentalCoroutinesApi
internal class MDCContextTest {

    companion object {
        const val ANOTHER_CONTEXT_KEY = "another-context-key"
    }

    @Test
    fun `should give the MDC you are looking for (default key)`() = runTest {
        val mdc = MDC()
        mdc["mdcKey"] = "mdcValue"

        MDCContext.withContext(mdc) {
            val result = MDCContext.read()
            assertEquals(mdc, result)
        }

        MDCContext.withContext(mdc) {
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context)
            assertEquals(mdc, result)
        }

        MDCContext.withContext(Context.of(mapOf("1" to "2")), mdc) {
            val size = coroutineContext[ReactorContext]?.context?.size()
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context)
            assertEquals(mdc, result)
            assertEquals(2, size)
        }
    }

    @Test
    fun `should give the MDC you are looking for (default key + use native withContext)`() = runTest {
        val mdcMap = mutableMapOf<String, String>()
        mdcMap["mdcKey"] = "mdcValue"

        withContext(Context.of(mapOf(DEFAULT_REACTOR_CONTEXT_MDC_KEY to mdcMap)).asCoroutineContext()) {
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context)
            assertEquals(mdcMap, result)
            assertEquals(mdcMap, result.map)
        }
    }

    @Test
    fun `should give the MDC you are looking for (another key)`() = runTest {
        val mdc2 = MDC(ANOTHER_CONTEXT_KEY)
        mdc2["mdcKey"] = "mdcValue"

        MDCContext.withContext(mdc2) {
            val result = MDCContext.read(ANOTHER_CONTEXT_KEY)
            assertEquals(mdc2, result)
        }
    }

    @Test
    fun `should give the MDC you are looking for (more MDC Context)`() = runTest {
        val mdc1 = MDC()
        mdc1["mdcKey"] = "mdcValue"

        val mdc2 = MDC(ANOTHER_CONTEXT_KEY)
        mdc2["mdcKey"] = "mdcValue"

        MDCContext.withContext(mdc1, mdc2) {
            val result1 = MDCContext.read(coroutineContext[ReactorContext]?.context)
            val result2 = MDCContext.read(coroutineContext[ReactorContext]?.context, DEFAULT_REACTOR_CONTEXT_MDC_KEY)
            val result3 = MDCContext.read(DEFAULT_REACTOR_CONTEXT_MDC_KEY)
            val result4 = MDCContext.read(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY)
            val result5 = MDCContext.read(ANOTHER_CONTEXT_KEY)

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

        MDCContext.withContext(MDC(mdcMap)) {
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context).map
            assertEquals(mdcMap, result)
        }

        MDCContext.withContext(MDC(ANOTHER_CONTEXT_KEY, mdcMap)) {
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY).map
            assertEquals(mdcMap, result)
        }
    }

    @Test
    fun `should be MDC stored with the overridden context ID`() = runTest {
        val mdcMap = mutableMapOf<String, String>()
        mdcMap["mdcKey"] = "mdcValue"

        MDCContext.withContext(MDC(mdcMap)) {
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context).map
            assertEquals(mdcMap, result)
        }

        MDCContext.withContext(MDC(ANOTHER_CONTEXT_KEY, mdcMap)) {
            val result = MDCContext.read(coroutineContext[ReactorContext]?.context, ANOTHER_CONTEXT_KEY).map
            assertEquals(mdcMap, result)
        }
    }

    @Test
    fun `should throw NullPointerException if any parameter is NULL`() = runTest {
        assertThrows<NullPointerException> {
            MDCContext.read(null)
        }

        assertThrows<NullPointerException> {
            MDCContext.read(null, ANOTHER_CONTEXT_KEY)
        }
    }

    @Test
    fun `should throw InvalidContextDataException if context data is invalid`() = runTest {
        withContext(Context.of(mapOf(Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY to 10)).asCoroutineContext()) {
            assertThrows<InvalidContextDataException> { MDCContext.read() }
        }

        withContext(Context.of(mapOf(Consts.DEFAULT_REACTOR_CONTEXT_MDC_KEY to "any")).asCoroutineContext()) {
            assertThrows<InvalidContextDataException> { MDCContext.read() }
        }
    }
}