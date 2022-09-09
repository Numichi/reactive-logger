package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.reactor.IReactorLogger
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView

abstract class ACoroutine<T : IReactorLogger>(
    override val mdcContextKey: String,
    override val scheduler: Scheduler
) : ICoroutineCore<T> {

    // TODO: remove
    abstract class Builder<L : Logger, R>(
        var logger: L,
        var contextExtractive: suspend () -> ContextView?,
        var scheduler: Scheduler,
        var mdcContextKey: String,
    ) {

        fun setLogger(logger: L): Builder<L, R> {
            this.logger = logger
            return this
        }

        fun setContextExtractive(contextExtractive: suspend () -> ContextView?): Builder<L, R> {
            this.contextExtractive = contextExtractive
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