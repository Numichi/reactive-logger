package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.abstracts.AReactive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler

class ReactiveLogger(
    override val logger: Logger,
    isEnableError: Boolean,
    mdcContextKey: String,
    scheduler: Scheduler,
) : AReactive(logger, isEnableError, mdcContextKey, scheduler),
    IReactorLogger {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(
        scheduler: Scheduler = DefaultValues.getInstance().defaultScheduler,
        mdcContextKey: String = DefaultValues.getInstance().defaultReactorContextMdcKey,
        enableError: Boolean = false,
        logger: Logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
    ) : AReactive.Builder<Logger, ReactiveLogger>(logger, scheduler, mdcContextKey, enableError) {
        override fun build(): ReactiveLogger {
            return ReactiveLogger(logger, enableError, mdcContextKey, scheduler)
        }
    }
}
