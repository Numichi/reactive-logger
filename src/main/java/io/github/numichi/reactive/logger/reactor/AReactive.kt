package io.github.numichi.reactive.logger.reactor

import org.slf4j.Logger
import reactor.core.scheduler.Scheduler

abstract class AReactive(
    override val logger: Logger,
    override val mdcContextKey: String,
    override val scheduler: Scheduler
) : IReactorCore {

    abstract class Builder<L : Logger, R>(
        var logger: L,
        var scheduler: Scheduler,
        var mdcContextKey: String,
    ) {

        fun setLogger(logger: L): Builder<L, R> {
            this.logger = logger
            return this
        }

        fun setScheduler(scheduler: Scheduler): Builder<L, R> {
            this.scheduler = scheduler
            return this
        }

        fun setMDCContextKey(mdcContextKey: String): Builder<L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this
        }

        abstract fun build(): R
    }
}