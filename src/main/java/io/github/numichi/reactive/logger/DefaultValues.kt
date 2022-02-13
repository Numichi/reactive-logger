package io.github.numichi.reactive.logger

import io.github.numichi.reactive.logger.exception.AlreadyConfigurationException
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

class DefaultValues private constructor(
    val defaultReactorContextMdcKey: String = DEFAULT_REACTOR_CONTEXT_MDC_KEY,
    val defaultScheduler: Scheduler = DEFAULT_SCHEDULER
) {

    private constructor(defaultScheduler: Scheduler) : this(DEFAULT_REACTOR_CONTEXT_MDC_KEY, defaultScheduler) {}

    companion object {
        private const val DEFAULT_REACTOR_CONTEXT_MDC_KEY = "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
        private val DEFAULT_SCHEDULER = Schedulers.boundedElastic()
        private var singleton: DefaultValues? = null

        @JvmStatic
        fun reset() {
            singleton = null
        }

        @JvmStatic
        fun getInstance(): DefaultValues {
            if (singleton == null) {
                configuration()
            }

            return singleton!!
        }

        @JvmStatic
        @Throws(AlreadyConfigurationException::class)
        fun configuration() {
            if (singleton == null) {
                singleton = DefaultValues()
            } else {
                throw AlreadyConfigurationException()
            }
        }

        @JvmStatic
        @Throws(AlreadyConfigurationException::class)
        fun configuration(defaultScheduler: Scheduler) {
            if (singleton == null) {
                singleton = DefaultValues(defaultScheduler)
            } else {
                throw AlreadyConfigurationException()
            }
        }

        @JvmStatic
        @Throws(AlreadyConfigurationException::class)
        fun configuration(defaultReactorContextMdcKey: String) {
            if (singleton == null) {
                singleton = DefaultValues(defaultReactorContextMdcKey)
            } else {
                throw AlreadyConfigurationException()
            }
        }

        @JvmStatic
        @Throws(AlreadyConfigurationException::class)
        fun configuration(defaultReactorContextMdcKey: String, defaultScheduler: Scheduler) {
            if (singleton == null) {
                singleton = DefaultValues(defaultReactorContextMdcKey, defaultScheduler)
            } else {
                throw AlreadyConfigurationException()
            }
        }
    }
}