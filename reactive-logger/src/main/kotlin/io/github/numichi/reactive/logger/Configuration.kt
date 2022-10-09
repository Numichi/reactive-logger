package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.hook.MDCHook
import io.github.numichi.reactive.logger.hook.MDCHookCache
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

object Configuration {
    @JvmStatic
    @Volatile
    var defaultReactorContextMdcKey: Any = DEFAULT_REACTOR_CONTEXT_MDC_KEY

    @JvmStatic
    @Volatile
    var defaultScheduler: Scheduler = Schedulers.boundedElastic()

    @JvmStatic
    fun <T> addGenericHook(name: String, contextKey: Any, order: Int, hook: (T?, MDC) -> Map<String, String?>) {
        MDCHookCache.addHook(MDCHook(name, contextKey, order, hook))
    }

    @JvmStatic
    fun <T> addGenericHook(name: String, contextKey: Any, hook: (T?, MDC) -> Map<String, String?>) {
        return addGenericHook(name, contextKey, 0, hook)
    }

    @JvmStatic
    fun addHook(name: String, contextKey: Any, order: Int, hook: (Any?, MDC) -> Map<String, String?>) {
        MDCHookCache.addHook(MDCHook(name, contextKey, order, hook))
    }

    @JvmStatic
    fun addHook(name: String, contextKey: Any, hook: (Any?, MDC) -> Map<String, String?>) {
        return addHook(name, contextKey, 0, hook)
    }

    @JvmStatic
    fun getHooks(): Map<String, MDCHook<*>> {
        return MDCHookCache.getHooks()
    }

    @JvmStatic
    fun existsHook(name: String): Boolean {
        return MDCHookCache.existsHook(name)
    }

    @JvmStatic
    fun removeHook(key: String) {
        MDCHookCache.removeHook(key)
    }

    @JvmStatic
    fun setDefaultScheduler(option: SchedulerOptions) {
        defaultScheduler = option.toScheduler()
    }

    @JvmStatic
    fun reset() {
        defaultReactorContextMdcKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY
        defaultScheduler = Schedulers.boundedElastic()
        MDCHookCache.clear()
    }
}