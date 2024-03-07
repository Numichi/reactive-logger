package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.hook.MDCContextHook
import io.github.numichi.reactive.logger.hook.MDCContextHookCache
import io.github.numichi.reactive.logger.hook.Position
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import reactor.util.context.ContextView

object Configuration {
    @JvmStatic
    @Volatile
    var defaultReactorContextMdcKey: Any = DEFAULT_REACTOR_CONTEXT_MDC_KEY

    @JvmStatic
    @Volatile
    var defaultScheduler: Scheduler = Schedulers.boundedElastic()

    @JvmStatic
    fun addHook(
        beforeSnapshot: Position,
        hook: (ContextView, MDC) -> Map<String, String?>,
    ) {
        return MDCContextHookCache.addHook(MDCContextHook(beforeSnapshot, hook))
    }

    @JvmStatic
    fun getContextHooks(): Map<Position, MDCContextHook> {
        return MDCContextHookCache.getHooks()
    }

    @JvmStatic
    fun existsHook(position: Position): Boolean {
        return MDCContextHookCache.existsHook(position)
    }

    @JvmStatic
    fun removeHook(position: Position) {
        MDCContextHookCache.removeHook(position)
    }

    @JvmStatic
    fun setDefaultScheduler(option: SchedulerOptions) {
        defaultScheduler = option.toScheduler()
    }

    @JvmStatic
    fun reset() {
        defaultReactorContextMdcKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY
        defaultScheduler = Schedulers.boundedElastic()
        MDCContextHookCache.clear()
    }
}
