package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.hook.MDCHookCache
import io.github.numichi.reactive.logger.hook.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.scheduler.Schedulers

class ConfigurationTest {

    @BeforeEach
    fun setUp() {
        Configuration.reset()
    }

    @Test
    fun `defaultReactorContextMdcKey modify test`() {
        assertEquals(DEFAULT_REACTOR_CONTEXT_MDC_KEY,  Configuration.defaultReactorContextMdcKey)

        Configuration.defaultReactorContextMdcKey = "foo"

        assertEquals("foo",  Configuration.defaultReactorContextMdcKey)
    }

    @Test
    fun `addHook, addGenericHook, removeHook and reset effects test`() {
        Configuration.addHook("key1", "foo1", -1) { it, _ -> mapOf("foo1" to "$it") }
        Configuration.addHook("key2", "foo2") { it, _ -> mapOf("foo2" to "$it") }
        Configuration.addGenericHook<String>("key3", "foo3", -1) { it, _ -> mapOf("foo3" to it!!) }
        Configuration.addGenericHook<String>("key4", "foo4") { it, _ -> mapOf("foo4" to it!!) }

        assertEquals(4, Configuration.getHooks().size)
        assertEquals(2, MDCHookCache.listBefore.size)
        assertEquals(2, MDCHookCache.listAfter.size)

        Configuration.addHook("key5", "foo5", -1) { it, _ -> mapOf("foo5" to "$it") }

        assertEquals(5, Configuration.getHooks().size)
        assertEquals(3, MDCHookCache.listBefore.size)
        assertEquals(2, MDCHookCache.listAfter.size)

        assertTrue(Configuration.existsHook("key4"))
        assertTrue(Configuration.existsHook("key3"))

        Configuration.removeHook("key4")
        Configuration.removeHook("key3")

        assertFalse(Configuration.existsHook("key4"))
        assertFalse(Configuration.existsHook("key3"))

        assertEquals(3, Configuration.getHooks().size)
        assertEquals(2, MDCHookCache.listBefore.size)
        assertEquals(1, MDCHookCache.listAfter.size)

        Configuration.reset()

        assertEquals(0, Configuration.getHooks().size)
        assertEquals(0, MDCHookCache.listBefore.size)
        assertEquals(0, MDCHookCache.listAfter.size)
    }

    @Test
    fun `MDCContextHook configuration by Configuration class`() {
        Configuration.addHook(Position.AFTER) { _, _ -> mapOf("foo1" to "bar1") }
        Configuration.addHook(Position.BEFORE) { _, _ -> mapOf("foo2" to "bar2") }

        assertEquals(2, Configuration.getContextHooks().size)
        assertEquals(true, Configuration.existsHook(Position.BEFORE))
        assertEquals(true, Configuration.existsHook(Position.AFTER))

        Configuration.removeHook(Position.AFTER)

        assertEquals(1, Configuration.getContextHooks().size)
        assertEquals(true, Configuration.existsHook(Position.BEFORE))
        assertEquals(false, Configuration.existsHook(Position.AFTER))

    }

    @Test
    fun `should be configuration scheduled from enum`() {
        Configuration.setDefaultScheduler(SchedulerOptions.SINGLE)
        assertSame(Schedulers.single(), Configuration.defaultScheduler)

        Configuration.setDefaultScheduler(SchedulerOptions.PARALLEL)
        assertSame(Schedulers.parallel(), Configuration.defaultScheduler)

        Configuration.setDefaultScheduler(SchedulerOptions.IMMEDIATE)
        assertSame(Schedulers.immediate(), Configuration.defaultScheduler)

        Configuration.setDefaultScheduler(SchedulerOptions.BOUNDED_ELASTIC)
        assertSame(Schedulers.boundedElastic(), Configuration.defaultScheduler)

        Configuration.defaultScheduler = Schedulers.single()
        assertSame(Schedulers.single(), Configuration.defaultScheduler)

        Configuration.defaultScheduler = Schedulers.parallel()
        assertSame(Schedulers.parallel(), Configuration.defaultScheduler)

        Configuration.defaultScheduler = Schedulers.immediate()
        assertSame(Schedulers.immediate(), Configuration.defaultScheduler)

        Configuration.defaultScheduler = Schedulers.boundedElastic()
        assertSame(Schedulers.boundedElastic(), Configuration.defaultScheduler)
    }
}