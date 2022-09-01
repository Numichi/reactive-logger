package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.hook.MDCHookCache
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
    fun `should store the added hooks and categorized after init`() {
        Configuration.addHook("key1", "foo1", 0) { it, _ ->  mapOf("foo1" to "$it") }
        Configuration.addHook("key2", "foo2", 0) { it, _ -> mapOf("foo2" to "$it") }
        Configuration.addGenericHook<String>("key3", "foo3", -1) { it, _ -> mapOf("foo3" to it!!) }

        assertEquals(3, Configuration.getHooks().size)
        assertEquals(0, MDCHookCache.listBefore.size)
        assertEquals(0, MDCHookCache.listAfter.size)

        MDCHookCache.initCache() // auto triggered
        assertEquals(3, Configuration.getHooks().size)
        assertEquals(1, MDCHookCache.listBefore.size)
        assertEquals(2, MDCHookCache.listAfter.size)

        Configuration.addHook("key4", "foo4", -1) { it, _ -> mapOf("foo4" to "$it") }

        assertEquals(4, Configuration.getHooks().size)
        assertEquals(1, MDCHookCache.listBefore.size)
        assertEquals(2, MDCHookCache.listAfter.size)

        MDCHookCache.initCache() // auto triggered
        assertEquals(4, Configuration.getHooks().size)
        assertEquals(2, MDCHookCache.listBefore.size)
        assertEquals(2, MDCHookCache.listAfter.size)

        assertTrue(Configuration.existsHook("key4"))
        assertTrue(Configuration.existsHook("key3"))
        Configuration.removeHook("key4")
        Configuration.removeHook("key3")
        assertFalse(Configuration.existsHook("key4"))
        assertFalse(Configuration.existsHook("key3"))

        MDCHookCache.initCache() // auto triggered
        assertEquals(2, Configuration.getHooks().size)
        assertEquals(0, MDCHookCache.listBefore.size)
        assertEquals(2, MDCHookCache.listAfter.size)

        Configuration.reset()
        assertEquals(0, Configuration.getHooks().size)
        assertEquals(0, MDCHookCache.listBefore.size)
        assertEquals(0, MDCHookCache.listAfter.size)
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
    }
}