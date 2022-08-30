package io.github.numichi.reactive.logger

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
    var customHook: MutableList<MDCHook<*>> = mutableListOf()

    fun <T> addGenericHook(contextKey: Any, order: Int = 0, hook: (T?) -> Map<String, String>) {
        customHook.add(MDCHook(contextKey, order, hook))
    }

    fun addHook(contextKey: Any, order: Int = 0, hook: (Any?) -> Map<String, String>) {
        customHook.add(MDCHook(contextKey, order, hook))
    }

    fun setDefaultScheduler(option: SchedulerOptions) {
        defaultScheduler = when (option) {
            SchedulerOptions.BOUNDED_ELASTIC -> Schedulers.boundedElastic()
            SchedulerOptions.PARALLEL -> Schedulers.parallel()
            SchedulerOptions.IMMEDIATE -> Schedulers.immediate()
            SchedulerOptions.SINGLE -> Schedulers.single()
        }
    }

    fun hookCacheClear() {
        MDCHookCache.initialized = false
    }

    fun reset() {
        defaultReactorContextMdcKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY
        defaultScheduler = Schedulers.boundedElastic()
        customHook = mutableListOf()
        hookCacheClear()
    }
}