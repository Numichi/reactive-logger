package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.DEFAULT_REACTOR_CONTEXT_MDC_KEY as DEFAULT_REACTOR_CONTEXT_MDC_KEY_FROM_COMMON
import io.github.numichi.reactive.logger.models.MDCHook
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.scheduler.Schedulers

class DefaultValuesTest {

    @BeforeEach
    fun setUp() {
        DefaultValues.getInstance().reset()
    }

    @Test
    fun `should be default configuration`() {
        val instance = DefaultValues.getInstance()

        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY_FROM_COMMON, instance.defaultReactorContextMdcKey)
        assertSame(Schedulers.boundedElastic(), instance.defaultScheduler)
        assertEquals(mutableListOf<MDCHook<*>>(), instance.customHook)
    }

    @Test
    fun `should be the value it set`() {
        val instance = DefaultValues.getInstance()
        instance.defaultReactorContextMdcKey = "mdc"

        val hook = MDCHook<Any>("anyContext", 1) { mapOf("a" to "b") }
        instance.customHook.add(hook)

        instance.defaultScheduler = Schedulers.immediate()

        assertEquals("mdc", instance.defaultReactorContextMdcKey)
        assertSame(Schedulers.immediate(), instance.defaultScheduler)
        assertEquals(listOf(hook), instance.customHook)
    }
}