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

        /**
         * Which scheduler does use for logging?
         *
         * @param scheduler Default: Schedulers.boundedElastic()
         */
        fun withScheduler(scheduler: Scheduler): Builder<L, R> {
            this.scheduler = scheduler
            return this;
        }

        /**
         * Which under reactor context key is the MDC stored?
         *
         * @param mdcContextKey Default: "DEFAULT_REACTOR_CONTEXT_MDC_KEY"
         */
        fun withMDCContextKey(mdcContextKey: String): Builder<L, R> {
            check(mdcContextKey.trim().isNotEmpty()) { "mdcContextKey must not be blank or empty" }
            this.mdcContextKey = mdcContextKey
            return this;
        }

        @Deprecated(
            message = "It can be misleading due to enabling word in method name and boolean parameter. Use: withError(boolean); False by default",
            replaceWith = ReplaceWith("withError(enableError)"),
            level = DeprecationLevel.WARNING
        )
        fun withEnableError(enable: Boolean): Builder<L, R> {
            this.enableError = enable
            return this
        }

        fun withError(enable: Boolean = true): Builder<L, R> {
            this.enableError = enable
            return this
        }

        fun withLogger(logger: L): Builder<L, R> {
            this.logger = logger
            return this;
        }

        abstract fun build(): R
    }
}