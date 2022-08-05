package io.github.numichi.reactive.logger.reactor

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

        fun setError(enableError: Boolean): Builder<L, R> {
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
        fun withScheduler(scheduler: Scheduler): Builder<L, R> {
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
        fun withMDCContextKey(mdcContextKey: String): Builder<L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setError(enable)"),
            level = DeprecationLevel.WARNING,
        )
        fun withEnableError(enable: Boolean): Builder<L, R> {
            this.enableError = enable
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setError(enable)"),
            level = DeprecationLevel.WARNING,
        )
        fun withError(enable: Boolean = true): Builder<L, R> {
            this.enableError = enable
            return this
        }

        @Deprecated(
            message = "Change name prefix from \"with\" to \"set\" for java builder pattern compatibility. It will be removed in version 2.3.0.",
            replaceWith = ReplaceWith("setLogger(logger)"),
            level = DeprecationLevel.WARNING,
        )
        fun withLogger(logger: L): Builder<L, R> {
            this.logger = logger
            return this
        }

        abstract fun build(): R
    }
}