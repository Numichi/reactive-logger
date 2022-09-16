package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.hook.MDCHook
import io.github.numichi.reactive.logger.hook.MDCHookCache
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

enum class SchedulerOptions {
    BOUNDED_ELASTIC,
    PARALLEL,
    IMMEDIATE,
    SINGLE
}

object Configuration {
    var defaultReactorContextMdcKey: String = DEFAULT_REACTOR_CONTEXT_MDC_KEY
    var defaultScheduler: Scheduler = Schedulers.boundedElastic()

    fun <T> addGenericHook(name: String, contextKey: Any, order: Int = 0, hook: (T?, MDC) -> Map<String, String>) {
        MDCHookCache.addHook(name, MDCHook(contextKey, order, hook))
    }

    fun addHook(name: String, contextKey: Any, order: Int = 0, hook: (Any?, MDC) -> Map<String, String>) {
        MDCHookCache.addHook(name, MDCHook(contextKey, order, hook))
    }

    fun getHooks(): Map<String, MDCHook<*>> {
        return MDCHookCache.getHooks()
    }

    fun existsHook(key: String): Boolean {
        return MDCHookCache.existsHook(key)
    }

    fun removeHook(key: String) {
        MDCHookCache.removeHook(key)
    }

    fun setDefaultScheduler(option: SchedulerOptions) {
        defaultScheduler = when (option) {
            SchedulerOptions.BOUNDED_ELASTIC -> Schedulers.boundedElastic()
            SchedulerOptions.PARALLEL -> Schedulers.parallel()
            SchedulerOptions.IMMEDIATE -> Schedulers.immediate()
            SchedulerOptions.SINGLE -> Schedulers.single()
        }
    }

    fun reset() {
        defaultReactorContextMdcKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY
        defaultScheduler = Schedulers.boundedElastic()
        MDCHookCache.clear()
    }
}