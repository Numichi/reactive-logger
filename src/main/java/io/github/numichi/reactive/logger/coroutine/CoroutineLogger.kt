package io.github.numichi.reactive.logger.coroutine

import io.github.numichi.reactive.logger.Configuration
import io.github.numichi.reactive.logger.reactor.IReactorLogger
import io.github.numichi.reactive.logger.reactor.ReactiveLogger
import kotlinx.coroutines.reactor.ReactorContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.scheduler.Scheduler
import reactor.util.context.ContextView
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class CoroutineLogger private constructor(
    override val reactorLogger: IReactorLogger,
    override val contextExtractive: suspend () -> ContextView?,
) : ICoroutineLogger<IReactorLogger>, ACoroutine<IReactorLogger>(
    reactorLogger.mdcContextKey,
    reactorLogger.scheduler
) {

    companion object {
        @JvmStatic
        @Deprecated(
            "CoroutineContext.Key is not need!",
            replaceWith = ReplaceWith("builder(contextExtractive)"),
            level = DeprecationLevel.WARNING
        )
        fun builder(key: CoroutineContext.Key<out CoroutineContext.Element>, contextExtractive: suspend () -> ContextView?): Builder {
            return Builder(contextExtractive)
        }


        @JvmStatic
        fun builder(contextExtractive: suspend () -> ContextView?): Builder {
            return Builder(contextExtractive)
        }

        @JvmStatic
        fun reactorBuilder() = builder { coroutineContext[ReactorContext]?.context }

        @JvmStatic
        fun getLogger(
            logger: Logger,
            contextKey: String? = null,
            scheduler: Scheduler? = null,
            contextExtractive: suspend () -> ContextView? = { coroutineContext[ReactorContext]?.context }
        ): CoroutineLogger {
            return CoroutineLogger(
                ReactiveLogger(
                    logger,
                    contextKey ?: Configuration.defaultReactorContextMdcKey,
                    scheduler ?: Configuration.defaultScheduler
                ),
                contextExtractive
            )
        }
    }

    // TODO: remove
    class Builder(
        contextExtractive: suspend () -> ContextView?,
        scheduler: Scheduler = Configuration.defaultScheduler,
        mdcContextKey: String = Configuration.defaultReactorContextMdcKey,
        logger: Logger = LoggerFactory.getLogger(ReactiveLogger::class.java)
    ) : ACoroutine.Builder<Logger, CoroutineLogger>(logger, contextExtractive, scheduler, mdcContextKey) {
        @Suppress("UNCHECKED_CAST")
        override fun build() = CoroutineLogger(
            ReactiveLogger(logger, mdcContextKey, scheduler),
            contextExtractive
        )
    }
}

