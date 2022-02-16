package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.reactor.IReactorLogger
import org.slf4j.Logger
import reactor.core.scheduler.Scheduler
import reactor.util.context.Context
import kotlin.coroutines.CoroutineContext

abstract class ACoroutine<T : IReactorLogger>(
    override val isEnableError: Boolean,
    override val mdcContextKey: String,
    override val scheduler: Scheduler
) : ICoroutineCore<T> {

    abstract class Builder<E : CCElement, L : Logger, R>(
        var logger: L,
        var contextKey: CCKey<E>,
        var contextExtractive: CCResolveFn<E>,
        var scheduler: Scheduler,
        var mdcContextKey: String,
        var enableError: Boolean,
    ) {
        fun withScheduler(scheduler: Scheduler): Builder<E, L, R> {
            this.scheduler = scheduler
            return this
        }

        fun withMDCContextKey(mdcContextKey: String): Builder<E, L, R> {
            this.mdcContextKey = mdcContextKey
            return this
        }

        fun withContextKey(contextKey: CoroutineContext.Key<E>): Builder<E, L, R> {
            this.contextKey = contextKey
            return this
        }

        fun withContextExtractive(contextExtractive: suspend (CoroutineContext.Key<out E>) -> Context?): Builder<E, L, R> {
            this.contextExtractive = contextExtractive
            return this
        }

        fun withEnableError(enableError: Boolean): Builder<E, L, R> {
            this.enableError = enableError
            return this
        }

        fun withLogger(logger: L): Builder<E, L, R> {
            this.logger = logger
            return this
        }

        abstract fun build(): R
    }
}