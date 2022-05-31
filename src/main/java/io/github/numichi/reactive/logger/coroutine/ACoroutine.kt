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
        /**
         * Which scheduler does use for logging?
         *
         * @param scheduler Default: Schedulers.boundedElastic()
         */
        fun withScheduler(scheduler: Scheduler): Builder<E, L, R> {
            this.scheduler = scheduler
            return this
        }

        /**
         * Which under reactor context key is the MDC stored?
         *
         * @param mdcContextKey Default: "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
         */
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

        @Deprecated(
            message = "It can be misleading due to enabling word in method name and boolean parameter. Use: withError(boolean); False by default",
            replaceWith = ReplaceWith("withError(enable)"),
            level = DeprecationLevel.WARNING
        )
        fun withEnableError(enable: Boolean): Builder<E, L, R> {
            this.enableError = enable
            return this
        }

        fun withError(enable: Boolean = true): Builder<E, L, R> {
            this.enableError = enable
            return this
        }

        fun withLogger(logger: L): Builder<E, L, R> {
            this.logger = logger
            return this
        }

        abstract fun build(): R
    }
}