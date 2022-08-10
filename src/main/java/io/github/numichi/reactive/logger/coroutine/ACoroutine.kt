package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.reactor.IReactorLogger
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler

abstract class ACoroutine<T : IReactorLogger>(
    override val mdcContextKey: String,
    override val scheduler: Scheduler
) : ICoroutineCore<T> {

    abstract class Builder<E : CCElement, L : Logger, R>(
        var logger: L,
        var contextKey: CCKey<E>,
        var contextExtractive: CCResolveFn<E>,
        var scheduler: Scheduler,
        var mdcContextKey: String,
    ) {

        fun setLogger(logger: L): Builder<E, L, R> {
            this.logger = logger
            return this
        }

        fun setContextKey(contextKey: CCKey<E>): Builder<E, L, R> {
            this.contextKey = contextKey
            return this
        }

        fun setContextExtractive(contextExtractive: CCResolveFn<E>): Builder<E, L, R> {
            this.contextExtractive = contextExtractive
            return this
        }

        fun setScheduler(scheduler: Scheduler): Builder<E, L, R> {
            this.scheduler = scheduler
            return this
        }

        fun setMDCContextKey(mdcContextKey: String): Builder<E, L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this
        }

        abstract fun build(): R
    }
}