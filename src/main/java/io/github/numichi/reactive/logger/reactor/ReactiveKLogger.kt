package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.DefaultValues
import io.github.numichi.reactive.logger.abstracts.AReactive
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler

class ReactiveKLogger(
    override val logger: KLogger,
    isEnableError: Boolean,
    mdcContextKey: String,
    scheduler: Scheduler,
) : AReactive(logger, isEnableError, mdcContextKey, scheduler),
    IReactorKLogger {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(
        scheduler: Scheduler = DefaultValues.getInstance().defaultScheduler,
        mdcContextKey: String = DefaultValues.getInstance().defaultReactorContextMdcKey,
        enableError: Boolean = false,
        logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveKLogger::class.java))
    ) : AReactive.Builder<KLogger, ReactiveKLogger>(logger, scheduler, mdcContextKey, enableError) {
        override fun build() = ReactiveKLogger(logger, enableError, mdcContextKey, scheduler)
    }
}
