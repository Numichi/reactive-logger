package io.github.numichi.reactive.logger.reactor

import io.github.numichi.reactive.logger.Configuration
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler

class ReactiveKLogger(
    override val logger: KLogger,
    mdcContextKey: String,
    scheduler: Scheduler,
) : AReactive(logger, mdcContextKey, scheduler),
    IReactorKLogger {

    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder(
        scheduler: Scheduler = Configuration.defaultScheduler,
        mdcContextKey: String = Configuration.defaultReactorContextMdcKey,
        logger: KLogger = KotlinLogging.logger(LoggerFactory.getLogger(ReactiveKLogger::class.java))
    ) : AReactive.Builder<KLogger, ReactiveKLogger>(logger, scheduler, mdcContextKey) {
        override fun build() = ReactiveKLogger(logger, mdcContextKey, scheduler)
    }
}
