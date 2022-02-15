package io.github.numichi.reactive.logger.abstracts

import io.github.numichi.reactive.logger.reactor.IReactorCore
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler

abstract class AReactive(
    override val logger: Logger,
    override val isEnableError: Boolean,
    override val mdcContextKey: String,
    override val scheduler: Scheduler
) : IReactorCore {

    abstract class Builder<L : Logger, R>(
        var logger: L,
        var scheduler: Scheduler,
        var mdcContextKey: String,
        var enableError: Boolean
    ) {

        fun withScheduler(scheduler: Scheduler): Builder<L, R> {
            this.scheduler = scheduler
            return this;
        }

        fun withMDCContextKey(mdcContextKey: String): Builder<L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this;
        }

        fun withEnableError(enableError: Boolean): Builder<L, R> {
            this.enableError = enableError
            return this;
        }

        fun withLogger(logger: L): Builder<L, R> {
            this.logger = logger
            return this;
        }

        abstract fun build(): R
    }
}