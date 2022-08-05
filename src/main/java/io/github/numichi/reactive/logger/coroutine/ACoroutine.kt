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

        fun setError(enableError: Boolean): Builder<E, L, R> {
            this.enableError = enableError
            return this
        }

        /**
         * Which scheduler does use for logging?
         *
         * @param scheduler Default: Schedulers.boundedElastic()
         */
        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setScheduler(scheduler)"),
            level = DeprecationLevel.WARNING,
        )
        fun withScheduler(scheduler: Scheduler): Builder<E, L, R> {
            this.scheduler = scheduler
            return this
        }

        /**
         * Which under reactor context key is the MDC stored?
         *
         * @param mdcContextKey Default: "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
         */
        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setMDCContextKey(mdcContextKey)"),
            level = DeprecationLevel.WARNING,
        )
        fun withMDCContextKey(mdcContextKey: String): Builder<E, L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setContextKey(contextKey)"),
            level = DeprecationLevel.WARNING,
        )
        fun withContextKey(contextKey: CoroutineContext.Key<E>): Builder<E, L, R> {
            this.contextKey = contextKey
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setContextExtractive(contextExtractive)"),
            level = DeprecationLevel.WARNING,
        )
        fun withContextExtractive(contextExtractive: suspend (CoroutineContext.Key<out E>) -> Context?): Builder<E, L, R> {
            this.contextExtractive = contextExtractive
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setError(enable)"),
            level = DeprecationLevel.WARNING,
        )
        fun withEnableError(enable: Boolean): Builder<E, L, R> {
            this.enableError = enable
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setError(enable)"),
            level = DeprecationLevel.WARNING
        )
        fun withError(enable: Boolean = true): Builder<E, L, R> {
            this.enableError = enable
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setLogger(logger)"),
            level = DeprecationLevel.WARNING,
        )
        fun withLogger(logger: L): Builder<E, L, R> {
            this.logger = logger
            return this
        }

        abstract fun build(): R
    }
}