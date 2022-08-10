package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.exception.AlreadyConfigurationException
import io.github.numichi.reactive.logger.models.MDCHook
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

class DefaultValues private constructor(
    var defaultReactorContextMdcKey: String = DEFAULT_REACTOR_CONTEXT_MDC_KEY,
    var defaultScheduler: Scheduler = Schedulers.boundedElastic(),
    var customHook: MutableList<MDCHook<*>> = mutableListOf()
) {

    @Throws(AlreadyConfigurationException::class)
    fun addHook(hook: MDCHook<*>) {
        customHook.add(hook)
    }

    fun removeHook(filter: (contextKey: Any) -> Boolean) {
        customHook = customHook.filter { filter(it.contextKey) }.toMutableList()
    }

    fun reset() {
        defaultReactorContextMdcKey = DEFAULT_REACTOR_CONTEXT_MDC_KEY
        defaultScheduler = Schedulers.boundedElastic()
        customHook = mutableListOf()
    }

    companion object {
        private var singleton: DefaultValues? = null

        @JvmStatic
        fun getInstance(): DefaultValues {
            if (singleton == null) {
                singleton = DefaultValues()
            }

            return singleton!!
        }
    }
}